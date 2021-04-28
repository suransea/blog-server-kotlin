package top.suransea.model

interface Timed {
    var ctime: Long
    var utime: Long
}

data class Article(
    var id: Int? = null,
    var title: String? = "",
    var summary: String? = "",
    var content: String? = "",
    override var ctime: Long = 0,
    override var utime: Long = 0,
) : Timed
