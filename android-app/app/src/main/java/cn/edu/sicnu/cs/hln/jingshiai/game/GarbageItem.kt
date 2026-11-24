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
 * 一张垃圾卡片的数据实体。
 *
 * @param name        垃圾名称，显示在卡片上
 * @param drawableRes 图片资源 id，0 表示暂时没有图片，界面显示占位色块
 * @param type        所属分类，取值见 [GarbageType]
 * @param imageKey    图片标识，与 drawable 里 trash_<imageKey>.png 对应；
 *                    后端只下发字符串标识，Android 端再映射成 drawableRes
 */
data class GarbageItem(
    val name: String,
    @param:DrawableRes val drawableRes: Int = 0,
    val type: Int,
    val imageKey: String = ""
)

/**
 * 游戏数据源。
 *
 * 第一版用本地写死的数据跑通拖拽、计分、判断流程；
 * 后续改成后端接口时，只需要把 [loadLocalGarbageList] 换成网络请求的返回结果，
 * 上层 GameActivity 与 GameAdapter 无需改动。
 */
object GarbageRepository {

    /**
     * 本地测试数据：12 张卡片，四类各 3 张，保证各种分类都能拖动验证。
     * 图片资源暂时为空（drawableRes = 0），界面用占位色块 + 名称调试；
     * 后续准备好图片后，把 drawableRes 填成 R.drawable.trash_xxx 即可。
     */
    fun loadLocalGarbageList(): List<GarbageItem> = listOf(
        // 可回收物
        GarbageItem(name = "塑料矿泉水瓶", type = GarbageType.RECYCLABLE, imageKey = "plastic_bottle"),
        GarbageItem(name = "易拉罐", type = GarbageType.RECYCLABLE, imageKey = "can"),
        GarbageItem(name = "旧纸箱", type = GarbageType.RECYCLABLE, imageKey = "carton"),

        // 厨余垃圾
        GarbageItem(name = "苹果核", type = GarbageType.KITCHEN, imageKey = "apple_core"),
        GarbageItem(name = "菜叶", type = GarbageType.KITCHEN, imageKey = "vegetable_leaf"),
        GarbageItem(name = "香蕉皮", type = GarbageType.KITCHEN, imageKey = "banana_peel"),

        // 有害垃圾
        GarbageItem(name = "废旧电池", type = GarbageType.HAZARDOUS, imageKey = "battery"),
        GarbageItem(name = "过期药品", type = GarbageType.HAZARDOUS, imageKey = "medicine"),
        GarbageItem(name = "荧光灯管", type = GarbageType.HAZARDOUS, imageKey = "fluorescent_tube"),

        // 其他垃圾
        GarbageItem(name = "用过的纸巾", type = GarbageType.OTHER, imageKey = "tissue"),
        GarbageItem(name = "陶瓷碎片", type = GarbageType.OTHER, imageKey = "ceramic"),
        GarbageItem(name = "烟头", type = GarbageType.OTHER, imageKey = "cigarette_butt")
    )

    /**
     * 预留：从后端获取题目。
     *
     * 后续接入 Retrofit 时取消注释并实现，例如：
     * suspend fun loadFromBackend(): List<GarbageItem> {
     *     val response = gameApi.getQuestions()
     *     return response.map { GarbageItem(name = it.name, type = it.category, imageKey = it.imageKey) }
     * }
     */
    // suspend fun loadFromBackend(): List<GarbageItem>
}
