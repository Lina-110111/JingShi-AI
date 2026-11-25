package cn.edu.sicnu.cs.hln.jingshiai.game

import androidx.annotation.DrawableRes

/**
 * 垃圾类型常量。
 *
 * 0 = 可回收物，1 = 厨余垃圾，2 = 有害垃圾，3 = 其他垃圾。
 * 数值与后端题库 JSON 的 category 字段保持一致，后续接接口时直接复用。
 */
object GarbageType {

    const val RECYCLABLE = 0
    const val KITCHEN = 1
    const val HAZARDOUS = 2
    const val OTHER = 3

    /** 类型数值转中文名称，用于提示语与结算展示 */
    fun displayName(type: Int): String = when (type) {
        RECYCLABLE -> "可回收物"
        KITCHEN -> "厨余垃圾"
        HAZARDOUS -> "有害垃圾"
        OTHER -> "其他垃圾"
        else -> "未知分类"
    }
}

/**
 * 三档难度模式。
 *
 * @param perType   每类抽几题（四类各取这么多，总数 = perType × 4）
 * @param maxLevel  可抽到的最高难度等级：1 简单、2 中等、3 困难
 */
enum class GarbageMode(
    val label: String,
    val desc: String,
    val perType: Int,
    val maxLevel: Int
) {
    EASY("简单", "8 题", perType = 2, maxLevel = 1),
    NORMAL("中等", "12 题", perType = 3, maxLevel = 2),
    HARD("困难", "16 题", perType = 4, maxLevel = 3);

    /** 一局总题数 */
    val totalCount: Int get() = perType * 4
}

/**
 * 一张垃圾卡片的数据实体。
 *
 * @param name        垃圾名称，显示在卡片上
 * @param emoji       卡片图标，用系统 emoji 渲染，和垃圾桶图标风格统一
 * @param type        所属分类，取值见 [GarbageType]
 * @param level       难度等级：1 基础、2 中等、3 困难，用于按模式抽题
 * @param drawableRes 真实图片资源 id，0 表示没有图片、界面用 emoji
 * @param imageKey    图片标识，与 drawable 里 trash_<imageKey>.png 对应；
 *                    后端只下发字符串标识，Android 端再映射成 drawableRes
 */
data class GarbageItem(
    val name: String,
    val emoji: String,
    val type: Int,
    val level: Int = 1,
    @param:DrawableRes val drawableRes: Int = 0,
    val imageKey: String = ""
)

/**
 * 游戏数据源。
 *
 * 第一版用本地写死的数据跑通拖拽、计分、判断流程；
 * 后续改成后端接口时，只需要把 [buildRound] 里的数据来源换成网络请求的返回结果，
 * 上层 GameActivity 与 GameAdapter 无需改动。
 */
object GarbageRepository {

    /** 全部题库：四类各 10 条，每条带难度等级 */
    private val ALL_GARBAGE: List<GarbageItem> = listOf(
        // ==================== 可回收物 ====================
        GarbageItem("塑料矿泉水瓶", "🧴", GarbageType.RECYCLABLE, level = 1, imageKey = "plastic_bottle"),
        GarbageItem("易拉罐", "🥫", GarbageType.RECYCLABLE, level = 1, imageKey = "can"),
        GarbageItem("旧纸箱", "📦", GarbageType.RECYCLABLE, level = 1, imageKey = "carton"),
        GarbageItem("旧报纸", "📰", GarbageType.RECYCLABLE, level = 2, imageKey = "newspaper"),
        GarbageItem("玻璃酒瓶", "🍾", GarbageType.RECYCLABLE, level = 2, imageKey = "glass_bottle"),
        GarbageItem("旧衣服", "👕", GarbageType.RECYCLABLE, level = 2, imageKey = "old_clothes"),
        GarbageItem("快递纸袋", "🛍", GarbageType.RECYCLABLE, level = 2, imageKey = "paper_bag"),
        GarbageItem("废旧手机", "📱", GarbageType.RECYCLABLE, level = 3, imageKey = "old_phone"),
        GarbageItem("塑料玩具", "🧸", GarbageType.RECYCLABLE, level = 3, imageKey = "plastic_toy"),
        GarbageItem("铝制饭盒", "🍱", GarbageType.RECYCLABLE, level = 3, imageKey = "aluminum_box"),

        // ==================== 厨余垃圾 ====================
        GarbageItem("苹果核", "🍎", GarbageType.KITCHEN, level = 1, imageKey = "apple_core"),
        GarbageItem("香蕉皮", "🍌", GarbageType.KITCHEN, level = 1, imageKey = "banana_peel"),
        GarbageItem("菜叶", "🥬", GarbageType.KITCHEN, level = 1, imageKey = "vegetable_leaf"),
        GarbageItem("剩饭剩菜", "🍚", GarbageType.KITCHEN, level = 2, imageKey = "leftover_rice"),
        GarbageItem("鸡蛋壳", "🥚", GarbageType.KITCHEN, level = 2, imageKey = "egg_shell"),
        GarbageItem("西瓜皮", "🍉", GarbageType.KITCHEN, level = 2, imageKey = "watermelon_rind"),
        GarbageItem("橘子皮", "🍊", GarbageType.KITCHEN, level = 2, imageKey = "orange_peel"),
        GarbageItem("鱼骨头", "🐟", GarbageType.KITCHEN, level = 3, imageKey = "fish_bone"),
        GarbageItem("茶叶渣", "🍵", GarbageType.KITCHEN, level = 3, imageKey = "tea_leaf"),
        GarbageItem("过期面包", "🍞", GarbageType.KITCHEN, level = 3, imageKey = "stale_bread"),

        // ==================== 有害垃圾 ====================
        GarbageItem("废旧电池", "🔋", GarbageType.HAZARDOUS, level = 1, imageKey = "battery"),
        GarbageItem("过期药品", "💊", GarbageType.HAZARDOUS, level = 1, imageKey = "medicine"),
        GarbageItem("荧光灯管", "💡", GarbageType.HAZARDOUS, level = 2, imageKey = "fluorescent_tube"),
        GarbageItem("水银温度计", "🌡", GarbageType.HAZARDOUS, level = 2, imageKey = "thermometer"),
        GarbageItem("油漆桶", "🎨", GarbageType.HAZARDOUS, level = 2, imageKey = "paint_bucket"),
        GarbageItem("杀虫剂", "🧪", GarbageType.HAZARDOUS, level = 2, imageKey = "pesticide"),
        GarbageItem("指甲油", "💅", GarbageType.HAZARDOUS, level = 3, imageKey = "nail_polish"),
        GarbageItem("废针管", "💉", GarbageType.HAZARDOUS, level = 3, imageKey = "syringe"),
        GarbageItem("过期化妆品", "💄", GarbageType.HAZARDOUS, level = 3, imageKey = "cosmetics"),
        GarbageItem("废胶片", "🎞", GarbageType.HAZARDOUS, level = 3, imageKey = "photo_film"),

        // ==================== 其他垃圾 ====================
        GarbageItem("用过的纸巾", "🧻", GarbageType.OTHER, level = 1, imageKey = "tissue"),
        GarbageItem("陶瓷碎片", "🏺", GarbageType.OTHER, level = 1, imageKey = "ceramic"),
        GarbageItem("烟头", "🚬", GarbageType.OTHER, level = 1, imageKey = "cigarette_butt"),
        GarbageItem("一次性餐具", "🥢", GarbageType.OTHER, level = 2, imageKey = "disposable_cutlery"),
        GarbageItem("大骨头", "🦴", GarbageType.OTHER, level = 2, imageKey = "big_bone"),
        GarbageItem("创可贴", "🩹", GarbageType.OTHER, level = 2, imageKey = "bandage"),
        GarbageItem("旧牙刷", "🪥", GarbageType.OTHER, level = 2, imageKey = "toothbrush"),
        GarbageItem("海绵", "🧽", GarbageType.OTHER, level = 3, imageKey = "sponge"),
        GarbageItem("猫砂", "🐾", GarbageType.OTHER, level = 3, imageKey = "cat_litter"),
        GarbageItem("脏抹布", "🧺", GarbageType.OTHER, level = 3, imageKey = "dirty_cloth")
    )

    /**
     * 按难度模式抽一局题目。
     *
     * 规则：四类各抽 [GarbageMode.perType] 条，只从 level <= [GarbageMode.maxLevel] 的题目里随机取，
     * 抽完把四类混在一起再整体打乱，避免出现"前几题全是同一类"。
     */
    fun buildRound(mode: GarbageMode): List<GarbageItem> {
        val selected = mutableListOf<GarbageItem>()

        GarbageType.all().forEach { type ->
            val sameType = ALL_GARBAGE.filter { it.type == type }
            val candidates = sameType
                .filter { it.level <= mode.maxLevel }
                .ifEmpty { sameType }
                .shuffled()

            selected += candidates.take(mode.perType)
        }

        return selected.shuffled()
    }

    /** 题库总条数，便于后续做"题库扩充"提示或调试 */
    val totalQuestionCount: Int get() = ALL_GARBAGE.size
}

/** 四类的数值列表，抽题时按类分组用 */
private fun GarbageType.all(): List<Int> = listOf(
    RECYCLABLE,
    KITCHEN,
    HAZARDOUS,
    OTHER
)
