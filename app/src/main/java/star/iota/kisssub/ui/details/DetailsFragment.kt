package star.iota.kisssub.ui.details

import android.annotation.SuppressLint
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.zzhoujay.richtext.RichText
import com.zzhoujay.richtext.ig.DefaultImageGetter
import kotlinx.android.synthetic.main.fragment_details.*
import star.iota.kisssub.R
import star.iota.kisssub.base.BaseFragment
import star.iota.kisssub.ext.addFragmentToActivity
import star.iota.kisssub.ui.item.ItemFragment
import star.iota.kisssub.ui.settings.ThemeHelper
import star.iota.kisssub.utils.SendUtils
import star.iota.kisssub.utils.ShareUtils
import star.iota.kisssub.widget.MessageBar

class DetailsFragment : BaseFragment(), DetailsContract.View {
    override fun success(bean: DetailsBean) {
        end()
        bindView(bean)
    }

    private fun resetView() {
        linearLayoutContainer.visibility = View.GONE
        textViewDetails.text = ""
        textViewList.text = ""
        textViewDesc.text = ""
        flexLayoutTags.removeAllViews()
    }

    @SuppressLint("InflateParams")
    private fun bindView(bean: DetailsBean) {
        linearLayoutContainer.visibility = View.VISIBLE
        RichText.from(bean.details).imageGetter(DefaultImageGetter()).urlClick {
            SendUtils.open(context!!, it)
            true
        }.into(textViewDetails)
        RichText.from(bean.tree).imageGetter(DefaultImageGetter()).into(textViewList)
        textViewDesc.text = bean.desc?.replace("，", "\n")
        bean.tags?.forEach { str ->
            val drawable = ContextCompat.getDrawable(context!!, R.drawable.bg_border) as GradientDrawable
            drawable.setColor(ThemeHelper.getColor(context!!))
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            val tag = LayoutInflater.from(context!!).inflate(R.layout.item_details_tag, null) as TextView
            tag.text = str
            tag.setCompoundDrawables(drawable, null, null, null)
            tag.setOnClickListener {
                (context!! as AppCompatActivity).addFragmentToActivity(ItemFragment.newSearchInstance("标签：$str", str), R.id.frameLayoutContainer)
            }
            flexLayoutTags.addView(tag)
        }
        buttonMagnet.setOnClickListener {
            SendUtils.copy(context!!, arguments!!.getString(TITLE), bean.magnet)
            SendUtils.open(context!!, bean.magnet)
            MessageBar.create(context!!, "已复制到剪切板，并尝试打开本地应用")
        }
        buttonTorrent.setOnClickListener {
            SendUtils.copy(context!!, arguments!!.getString(TITLE), bean.torrent)
            SendUtils.open(context!!, bean.torrent)
            MessageBar.create(context!!, "已复制到剪切板，并尝试打开本地应用")
        }
        buttonShare.setOnClickListener {
            ShareUtils.share(context!!, "\n 标题：${arguments!!.getString(TITLE)}\n\n" +
                    "磁链：${bean.magnet}\n\n" +
                    "种链：${bean.torrent}")
        }
    }

    override fun error(e: String?) {
        end()
        MessageBar.create(context!!, e)
    }

    override fun noData() {
        end()
        MessageBar.create(context!!, "没有获得数据")
    }

    companion object {
        val URL = "url"
        val TITLE = "title"
        fun newInstance(title: String, url: String): DetailsFragment {
            val fragment = DetailsFragment()
            val bundle = Bundle()
            bundle.putString(TITLE, title)
            bundle.putString(URL, url)
            fragment.arguments = bundle
            return fragment
        }
    }

    private fun end() {
        isLoading = false
        refreshLayout.finishRefreshing()
    }


    override fun getContainerViewId(): Int = R.layout.fragment_details

    private var url: String? = null
    override fun doSome() {
        setToolbarTitle(arguments!!.getString(TITLE))
        url = arguments!!.getString(URL)
        initPresenter()
        initRefreshLayout()
    }

    private lateinit var presenter: DetailsPresenter
    private fun initPresenter() {
        presenter = DetailsPresenter(this)
    }

    private var isLoading = false
    private fun initRefreshLayout() {
        refreshLayout.startRefresh()
        refreshLayout.setEnableLoadmore(false)
        refreshLayout.setOnRefreshListener(object : RefreshListenerAdapter() {
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                if (!checkIsLoading()) {
                    isLoading = true
                    resetView()
                    presenter.get(url!!)
                }
            }
        })
    }

    private fun checkIsLoading(): Boolean {
        if (isLoading) {
            MessageBar.create(context!!, "数据正在加载中，请等待...")
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unsubscribe()
        RichText.recycle()
    }

}