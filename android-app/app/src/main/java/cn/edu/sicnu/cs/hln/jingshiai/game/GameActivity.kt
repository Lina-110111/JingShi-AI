package cn.edu.sicnu.cs.hln.jingshiai.game

import android.animation.ObjectAnimator
import android.content.ClipData
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.edu.sicnu.cs.hln.jingshiai.R
import cn.edu.sicnu.cs.hln.jingshiai.databinding.ActivityGameBinding

/**
 * 垃圾分类闯关主页面。
 *
 * 闯关规则：
 * 1. 三档难度按 简单 → 中等 → 困难 顺序解锁，必须先通过前一关才能进入下一关；
 * 2. 简单 / 中等最多允许答错 2 次，困难最多 3 次，超过即本关失败、整局结束；
 * 3. 答完全部题目且错误次数未超限，视为通过本关，解锁下一难度。
 *
 * 交互流程：
 * 长按底部卡片启动系统拖拽（startDragAndDrop），松手落在垃圾桶上判定分类；
 * 分类正确 +10 分，错误 -5 分（最低 0 分）并消耗一次错误额度。
 */
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var adapter: GameAdapter

    /** 计分规则 */
    private val scorePerCorrect = 10
    private val scorePerWrong = 5

    private var score = 0
    private var correctCount = 0
    private var wrongCount = 0
    private var totalCount = 0

    /** 已解锁的最高关卡序号：0 只有简单、1 到中等、2 全解锁（仅本次进入有效） */
    private var unlockedLevel = 0

    /** 当前关卡，进入时固定从简单开始 */
    private var mode: GarbageMode = GarbageMode.EASY

    /** 本关是否已结束（通关或失败），结束后不再响应拖拽 */
    private var roundFinished = false

    /** 当前拖拽中的卡片与它在列表中的位置 */
    private var draggingItem: GarbageItem? = null
    private var draggingIndex = -1

    /** 一次拖拽只能判分一次，避免 drop 与 drag ended 重复触发 */
    private var isJudging = false

    /** 桶视图 → 桶对应的分类 */
    private lateinit var binViews: List<Pair<View, Int>>

    /** 每个桶的原始背景，用于拖拽高亮结束后恢复 */
    private val originBinBackground = mutableMapOf<View, Int>()

    /** 模式按钮 → 对应模式 */
    private lateinit var modeChips: List<Pair<TextView, GarbageMode>>

    /** 反馈文字自动隐藏任务 */
    private val hideFeedbackRunnable = Runnable {
        binding.tvFeedback.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction { binding.tvFeedback.visibility = View.INVISIBLE }
            .start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener { finish() }

        // 每次进入游戏都只有简单关可玩，通关后在同一次游戏内解锁下一关
        unlockedLevel = 0
        mode = GarbageMode.EASY

        setupModeChips()
        setupRecyclerView()
        setupBins()
        startRound(mode)
    }

    /** 三档难度按钮：只能点已解锁的关卡 */
    private fun setupModeChips() {
        modeChips = listOf(
            binding.chipEasy to GarbageMode.EASY,
            binding.chipNormal to GarbageMode.NORMAL,
            binding.chipHard to GarbageMode.HARD
        )

        modeChips.forEach { (chip, chipMode) ->
            chip.setOnClickListener {
                when {
                    !isUnlocked(chipMode) -> {
                        val previous = GarbageMode.values().getOrNull(chipMode.ordinal - 1)
                        val tip = getString(
                            R.string.game_feedback_locked,
                            previous?.label ?: GarbageMode.EASY.label,
                            chipMode.label
                        )
                        showFeedback(tip, false)
                        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show()
                    }

                    chipMode == mode -> Unit

                    else -> {
                        mode = chipMode
                        startRound(chipMode)
                    }
                }
            }
        }
    }

    /** 初始化底部横向卡片列表 */
    private fun setupRecyclerView() {
        adapter = GameAdapter(emptyList()) { view, item, position ->
            startDrag(view, item, position)
        }

        binding.rvGarbage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvGarbage.adapter = adapter
    }

    /** 开始新一关：重置计分、按模式抽题、打乱顺序、刷新界面 */
    private fun startRound(target: GarbageMode) {
        resetDragState()

        score = 0
        correctCount = 0
        wrongCount = 0
        roundFinished = false

        // 抽题时已在 GarbageRepository 里做过分类均衡 + 整体打乱
        val roundItems = GarbageRepository.buildRound(target)
        totalCount = roundItems.size
        adapter.submit(roundItems)

        updateScoreUi()
        updateModeChips()
        showFeedback(
            getString(R.string.game_feedback_round_start, target.label, totalCount, target.maxWrong),
            null
        )

        binding.rvGarbage.scrollToPosition(0)
    }

    /** 是否已解锁某一关 */
    private fun isUnlocked(target: GarbageMode): Boolean = target.ordinal <= unlockedLevel

    /** 通关后解锁下一关（只在本次游戏内有效，退出重进仍从简单开始） */
    private fun unlockNext(target: GarbageMode) {
        val next = target.next() ?: return
        if (next.ordinal <= unlockedLevel) return

        unlockedLevel = next.ordinal
    }

    /** 刷新三档按钮：选中 / 可点 / 锁定三种外观 */
    private fun updateModeChips() {
        modeChips.forEach { (chip, chipMode) ->
            val unlocked = isUnlocked(chipMode)
            val selected = unlocked && chipMode == mode

            chip.setBackgroundResource(
                when {
                    selected -> R.drawable.game_bg_chip_selected
                    unlocked -> R.drawable.game_bg_chip_normal
                    else -> R.drawable.game_bg_chip_locked
                }
            )
            chip.setTextColor(
                ContextCompat.getColor(
                    this,
                    when {
                        selected -> R.color.game_card_white
                        unlocked -> R.color.game_primary_green
                        else -> R.color.game_locked_text
                    }
                )
            )
            chip.text = if (unlocked) {
                "${chipMode.label} ${chipMode.desc}"
            } else {
                getString(R.string.game_mode_locked_format, chipMode.label)
            }
        }
    }

    /** 初始化四个垃圾桶的拖拽监听 */
    private fun setupBins() {
        originBinBackground[binding.binRecyclable] = R.drawable.game_bg_bin_recyclable
        originBinBackground[binding.binKitchen] = R.drawable.game_bg_bin_kitchen
        originBinBackground[binding.binHazardous] = R.drawable.game_bg_bin_hazardous
        originBinBackground[binding.binOther] = R.drawable.game_bg_bin_other

        binViews = listOf(
            binding.binRecyclable to GarbageType.RECYCLABLE,
            binding.binKitchen to GarbageType.KITCHEN,
            binding.binHazardous to GarbageType.HAZARDOUS,
            binding.binOther to GarbageType.OTHER
        )

        binViews.forEach { (bin, type) ->
            bin.setOnDragListener { view, event -> handleBinDrag(view, type, event) }
        }
    }

    /** 长按卡片，启动系统原生拖拽 */
    private fun startDrag(view: View, item: GarbageItem, position: Int) {
        if (isJudging || roundFinished) return

        draggingItem = item
        draggingIndex = position

        val shadow = View.DragShadowBuilder(view)
        val clipData = ClipData.newPlainText("garbage", item.name)
        // localState 直接携带数据对象，桶里松手时取出来判分
        view.startDragAndDrop(clipData, shadow, item, 0)

        // 拖动时卡片半透明，视觉上跟随手指
        view.alpha = 0.6f
    }

    /**
     * 桶的拖拽事件处理。
     * ACTION_DRAG_STARTED 必须返回 true，否则后续事件不会派发给该视图。
     */
    private fun handleBinDrag(bin: View, binType: Int, event: DragEvent): Boolean {
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> return true

            DragEvent.ACTION_DRAG_ENTERED -> setBinHighlight(bin, true)

            DragEvent.ACTION_DRAG_EXITED -> setBinHighlight(bin, false)

            DragEvent.ACTION_DROP -> {
                setBinHighlight(bin, false)
                val item = event.localState as? GarbageItem
                if (item != null) {
                    judge(item, binType, bin)
                }
            }

            DragEvent.ACTION_DRAG_ENDED -> {
                setBinHighlight(bin, false)
                if (!event.result) {
                    // 松手位置不在任何垃圾桶上：不判分，只给提示
                    showFeedback(getString(R.string.game_feedback_outside), null)
                }
                resetDragState()
            }
        }
        return true
    }

    /** 拖拽进入 / 离开桶时的背景高亮 */
    private fun setBinHighlight(bin: View, highlighted: Boolean) {
        val resId = if (highlighted) {
            R.drawable.game_bg_bin_active
        } else {
            originBinBackground[bin] ?: R.drawable.game_bg_bin_active
        }
        bin.setBackgroundResource(resId)
    }

    /** 判定分类、计分、消耗错误额度、移除卡片，并判断是否通关或失败 */
    private fun judge(item: GarbageItem, binType: Int, bin: View) {
        if (isJudging || roundFinished) return
        isJudging = true

        val correct = item.type == binType

        if (correct) {
            score += scorePerCorrect
            correctCount++
            playBinBounce(bin)
            showFeedback(
                getString(
                    R.string.game_feedback_correct,
                    item.name,
                    GarbageType.displayName(item.type),
                    scorePerCorrect
                ),
                true
            )
        } else {
            score = (score - scorePerWrong).coerceAtLeast(0)
            wrongCount++
            playBinShake(bin)
            showFeedback(
                getString(
                    R.string.game_feedback_wrong,
                    item.name,
                    GarbageType.displayName(item.type),
                    scorePerWrong
                ),
                false
            )
        }

        updateScoreUi()

        // 错误次数超过本关额度：整局直接失败，卡片留在原位
        if (!correct && wrongCount > mode.maxWrong) {
            roundFinished = true
            binding.tvFeedback.removeCallbacks(hideFeedbackRunnable)
            binding.tvFeedback.visibility = View.VISIBLE
            binding.tvFeedback.alpha = 1f
            showFeedback(getString(R.string.game_feedback_fail, wrongCount, mode.maxWrong), false)
            binding.rvGarbage.postDelayed({ showFailDialog() }, 500)
            return
        }

        // 作答完成后移除该卡片，post 避免在 RecyclerView 布局计算中直接改动数据
        val index = draggingIndex
        if (index >= 0) {
            binding.rvGarbage.post {
                adapter.removeAt(index)
                if (adapter.remainingCount() == 0) {
                    roundFinished = true
                    binding.rvGarbage.postDelayed({ onRoundCleared() }, 400)
                }
            }
        }
    }

    /** 一次拖拽结束，清理临时状态 */
    private fun resetDragState() {
        draggingItem = null
        draggingIndex = -1
        isJudging = false

        // 恢复所有卡片的透明度（拖拽中途松手时卡片可能还是半透明的）
        for (i in 0 until binding.rvGarbage.childCount) {
            binding.rvGarbage.getChildAt(i).alpha = 1f
        }
    }

    /** 更新顶部分数、正确数、错误次数（含本关额度）与进度 */
    private fun updateScoreUi() {
        binding.tvScore.text = score.toString()
        binding.tvCorrect.text = correctCount.toString()
        binding.tvWrong.text = getString(R.string.game_wrong_value, wrongCount, mode.maxWrong)
        binding.tvWrong.setTextColor(
            ContextCompat.getColor(
                this,
                if (wrongCount > mode.maxWrong) R.color.game_warn else R.color.game_text_body
            )
        )
        binding.tvProgress.text =
            getString(R.string.game_progress_value, correctCount + wrongCount, totalCount)
    }

    /**
     * 页面内文字反馈。
     * @param isCorrect true 绿色、false 红色、null 中性提示色
     */
    private fun showFeedback(text: String, isCorrect: Boolean?) {
        val colorRes = when (isCorrect) {
            true -> R.color.game_primary_green
            false -> R.color.game_warn
            null -> R.color.game_text_hint
        }

        binding.tvFeedback.removeCallbacks(hideFeedbackRunnable)
        binding.tvFeedback.animate().cancel()
        binding.tvFeedback.text = text
        binding.tvFeedback.setTextColor(ContextCompat.getColor(this, colorRes))
        binding.tvFeedback.visibility = View.VISIBLE
        binding.tvFeedback.alpha = 1f

        if (isCorrect != null) {
            binding.tvFeedback.postDelayed(hideFeedbackRunnable, 1800)
        }
    }

    /** 答对：桶轻微放大回弹 */
    private fun playBinBounce(bin: View) {
        bin.animate().cancel()
        bin.animate()
            .scaleX(1.08f)
            .scaleY(1.08f)
            .setDuration(120)
            .setInterpolator(OvershootInterpolator())
            .withEndAction {
                bin.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
            }
            .start()
    }

    /** 答错：桶左右抖动 */
    private fun playBinShake(bin: View) {
        val base = bin.translationX
        ObjectAnimator.ofFloat(
            bin,
            "translationX",
            base,
            base + 14f,
            base - 14f,
            base + 8f,
            base - 8f,
            base
        ).apply {
            duration = 260
            start()
        }
    }

    /** 题目全部答完且未超错额：解锁下一关或提示全部通关 */
    private fun onRoundCleared() {
        if (isFinishing || isDestroyed) return

        val next = mode.next()
        if (next == null) {
            showAllPassedDialog()
        } else {
            unlockNext(mode)
            updateModeChips()
            showPassDialog(next)
        }
    }

    /** 通关弹窗：直接进入下一关 */
    private fun showPassDialog(next: GarbageMode) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.game_pass_title, mode.label))
            .setMessage(
                getString(
                    R.string.game_pass_message,
                    score,
                    correctCount,
                    wrongCount,
                    next.label,
                    next.totalCount
                )
            )
            .setCancelable(false)
            .setPositiveButton(getString(R.string.game_next_mode, next.label)) { _, _ ->
                mode = next
                startRound(next)
            }
            .setNegativeButton(R.string.game_exit) { _, _ -> finish() }
            .show()
    }

    /** 三关全部通过 */
    private fun showAllPassedDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.game_all_pass_title)
            .setMessage(
                getString(
                    R.string.game_all_pass_message,
                    score,
                    correctCount,
                    wrongCount
                )
            )
            .setCancelable(false)
            .setPositiveButton(R.string.game_play_again) { _, _ -> startRound(mode) }
            .setNegativeButton(R.string.game_exit) { _, _ -> finish() }
            .show()
    }

    /** 错误次数超限：挑战失败 */
    private fun showFailDialog() {
        if (isFinishing || isDestroyed) return

        AlertDialog.Builder(this)
            .setTitle(R.string.game_fail_title)
            .setMessage(getString(R.string.game_fail_message, mode.label, mode.maxWrong, wrongCount))
            .setCancelable(false)
            .setPositiveButton(R.string.game_retry) { _, _ -> startRound(mode) }
            .setNegativeButton(R.string.game_exit) { _, _ -> finish() }
            .show()
    }

    override fun onDestroy() {
        binding.tvFeedback.removeCallbacks(hideFeedbackRunnable)
        super.onDestroy()
    }
}
