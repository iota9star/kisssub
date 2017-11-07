package star.iota.kisssub.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment : Fragment() {


    protected abstract fun getContainerViewId(): Int

    protected abstract fun doSome()

    private var preTitle: String? = null

    protected fun setToolbarTitle(title: CharSequence?) {
        (this.activity!! as BaseActivity).supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preTitle = (this.activity!! as BaseActivity).supportActionBar?.title?.toString()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getContainerViewId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        doSome()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (preTitle != null) {
            setToolbarTitle(preTitle!!)
        }
    }
}