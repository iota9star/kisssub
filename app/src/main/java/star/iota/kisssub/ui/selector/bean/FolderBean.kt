package star.iota.kisssub.ui.selector.bean

class FolderBean {
    var dir: String? = null
        set(dir) {
            field = dir
            val index = this.dir!!.lastIndexOf("/")
            this.name = this.dir!!.substring(index)
        }
    var firstImgPath: String? = null
    var name: String? = null
        private set
    var count: Int = 0
}
