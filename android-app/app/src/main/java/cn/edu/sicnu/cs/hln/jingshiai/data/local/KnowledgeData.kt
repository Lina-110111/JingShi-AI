package cn.edu.sicnu.cs.hln.jingshiai.data.local

// 一条垃圾分类知识
data class KnowledgeItem(
    val id: Int,
    val category: String,
    val title: String,
    val content: String
)

// 本地预置的分类知识，避免使用随机或编造的规则
object KnowledgeData {

    val items: List<KnowledgeItem> = listOf(
        KnowledgeItem(
            id = 1,
            category = "可回收物",
            title = "塑料瓶要清空后投放",
            content = "塑料瓶属于可回收物，投放前应清空瓶内液体并简单冲洗，压扁后再投入可回收物收集容器。"
        ),
        KnowledgeItem(
            id = 2,
            category = "厨余垃圾",
            title = "果皮菜叶属于厨余垃圾",
            content = "剩菜剩饭、果皮、菜叶、茶叶渣等易腐烂的生物质废弃物应投入厨余垃圾收集容器，投放前尽量沥干水分。"
        ),
        KnowledgeItem(
            id = 3,
            category = "有害垃圾",
            title = "废旧电池请单独投放",
            content = "废电池、废灯管、过期药品等含有有害物质的垃圾，应单独投放至有害垃圾收集容器，不要混入其他垃圾。"
        ),
        KnowledgeItem(
            id = 4,
            category = "其他垃圾",
            title = "用过的纸巾属于其他垃圾",
            content = "用过的纸巾、陶瓷碎片、一次性餐具等难以回收且无危害的垃圾，投入其他垃圾收集容器。"
        ),
        KnowledgeItem(
            id = 5,
            category = "可回收物",
            title = "快递纸箱请压扁后投放",
            content = "快递纸箱属于可回收物，撕掉胶带并压扁后投放，可以节省回收空间，提高回收效率。"
        )
    )

    // 按一年中的第几天取一条，保证同一天展示的内容固定
    fun todayItem(dayOfYear: Int): KnowledgeItem = items[dayOfYear % items.size]
}
