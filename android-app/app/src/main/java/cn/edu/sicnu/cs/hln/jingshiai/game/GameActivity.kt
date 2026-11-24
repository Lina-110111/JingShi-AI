package cn.edu.sicnu.cs.hln.jingshiai.game

import android.animation.ObjectAnimator
import android.content.ClipData
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.edu.sicnu.cs.hln.jingshiai.R
import cn.edu.sicnu.cs.hln.jingshiai.databinding.ActivityGameBinding

/**
 * 垃圾分类拖拽主页面。
 *
 * 交互流程：
 * 1. 底部横向列表展示垃圾卡片，长按卡片启动系统拖拽（startDragAndDrop）；
 * 2. 上方四个垃圾桶通过 OnDragListener 接收拖拽事件，松手时判定分类；
 * 3. 分类正确 +10 分，分类错误 -5 分（最低 0 分），并给出文字反馈与桶动画；
 * 4. 卡片作答后从列表移除，全部答完弹出结算对话框。
 *
 * 判分与数据都在本地完成，后续接后端时只需替换 GarbageRepository 的数据来源。
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

    /** 当前拖拽中的卡片与它在列表中的位置 */
    private var draggingItem: GarbageItem? = null
    private var draggingIndex = -1

    /** 一次拖拽只能判分一次，避免 drop 与 drag ended 重复触发 */
    private var isJudging = false

    /** 桶视图 → 桶对应的分类 */
    private lateinit var binViews: List<Pair<View, Int>>

    /** 每个桶的原始背景，用于拖拽高亮结束后恢复 */
    private val originBinBackground = mutableMapOf<View, Int>()

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

        setupRecyclerView()
        setupBins()
        updateScoreUi()

        showFeedback(getString(R.string.game_feedback_default), null)
    }

    /** 初始化底部横向卡片列表 */
    private fun setupRecyclerView() {
        val garbageList = GarbageRepository.loadLocalGarbageList()
        totalCount = garbageList.size

        adapter = GameAdapter(garbageList) { view, item, position ->
            startDrag(view, item, position)
        }

        binding.rvGarbage.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvGarbage.adapter = adapter
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
        if (isJudging) return

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

    /** 判定分类、计分、播放反馈动画、移除卡片 */
    private fun judge(item: GarbageItem, binType: Int, bin: View) {
        if (isJudging) return
        isJudging = true

        if (item.type == binType) {
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

        // 作答完成后移除该卡片，post 避免在 RecyclerView 布局计算中直接改动数据
        val index = draggingIndex
        if (index >= 0) {
            binding.rvGarbage.post {
                adapter.removeAt(index)
                if (adapter.remainingCount() == 0) {
                    binding.rvGarbage.postDelayed({ showResultDialog() }, 300)
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

    /** 更新顶部分数、正确数、错误数、进度 */
    private fun updateScoreUi() {
        binding.tvScore.text = score.toString()
        binding.tvCorrect.text = correctCount.toString()
        binding.tvWrong.text = wrongCount.toString()
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

    /** 全部答完弹出结算对话框 */
    private fun showResultDialog() {
        if (isFinishing || isDestroyed) return

        val answered = correctCount + wrongCount
        val accuracy = if (answered == 0) 0 else correctCount * 100 / answered

        AlertDialog.Builder(this)
            .setTitle(R.string.game_result_title)
            .setMessage(
                getString(
                    R.string.game_result_message,
                    score,
                    correctCount,
                    wrongCount,
                    accuracy
                )
            )
            .setCancelable(false)
            .setPositiveButton(R.string.game_play_again) { _, _ -> recreate() }
            .setNegativeButton(R.string.game_exit) { _, _ -> finish() }
            .show()
    }

    override fun onDestroy() {
        binding.tvFeedback.removeCallbacks(hideFeedbackRunnable)
        super.onDestroy()
    }
}
