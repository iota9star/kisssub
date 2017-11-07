package star.iota.kisssub.utils

import android.content.Context
import android.content.Intent

object ShareUtils{
    fun share(context: Context,content:String){
        val intent= Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,content)
        intent.type = "text/plain"
        context.startActivity(Intent.createChooser(intent,"o(*≧▽≦)ツ"))
    }
}