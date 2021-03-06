package io.reyurnible.android.workrise.presentation.top

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.*
import dagger.android.support.AndroidSupportInjection
import io.reyurnible.android.workrise.R
import io.reyurnible.android.workrise.domain.model.entity.Report
import io.reyurnible.android.workrise.domain.model.value.YearMonthDay
import io.reyurnible.android.workrise.extensions.toVisible
import io.reyurnible.android.workrise.presentation.reportsetting.ReportSettingActivity
import io.reyurnible.android.workrise.presentation.reportsetting.createInstance
import kotlinx.android.synthetic.main.top_fragment.*
import javax.inject.Inject

/**
 * トップ画面
 */
class TopFragment : Fragment(), TopPresenter.TopView {
    companion object;

    @Inject lateinit var presenter: TopPresenter
    private lateinit var tabAdapter: TopTabAdapter
    private lateinit var pagerAdapter: TopPagerAdapter

    override fun onAttach(context: Context?) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.top_fragment, container, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
        presenter.initialize(this)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.top_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
            when (item?.itemId) {
                R.id.top_menu_setting -> {
                    startActivity(ReportSettingActivity.createInstance(activity))
                    true
                }
                else -> false
            }

    override fun onDestroyView() {
        presenter.release()
        super.onDestroyView()
    }

    override fun setDailyReportList(dailyReportList: List<Pair<YearMonthDay, Report?>>) {
        dailyReportList.map { it.first }.let { dates ->
            pagerAdapter.dates = dates
            tabAdapter.dates = dates
        }
        pagerAdapter.notifyDataSetChanged()
        tabAdapter.notifyDataSetChanged()
    }

    override fun setSubTitle(date: YearMonthDay) {
        yearMonthText.text = getString(R.string.top_sub_title, date.year, date.month)
    }

    override fun setCurrentPosition(position: Int, animate: Boolean) {
        viewPager.setCurrentItem(position, animate)
    }

    override fun setCurrentPositionStatus(isToday: Boolean) {
        backPositionButton.visibility = isToday.not().toVisible()
    }

    private fun initializeView() {
        pagerAdapter = TopPagerAdapter(fragmentManager, dates = emptyList<YearMonthDay>())
        viewPager.apply {
            adapter = pagerAdapter
            offscreenPageLimit = 2
        }
        tabAdapter = TopTabAdapter(viewPager, dates = emptyList<YearMonthDay>())
        tabLayout.setUpWithAdapter(tabAdapter)
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                presenter.changePage(position)
            }
        })
        backPositionButton.setOnClickListener {
            presenter.clickBackToday()
        }
    }

}
