package io.reyurnible.android.workrise.presentation.report

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reyurnible.android.workrise.common.addDisposableToBag
import io.reyurnible.android.workrise.domain.model.entity.Report
import io.reyurnible.android.workrise.domain.model.identifier.ReportId
import io.reyurnible.android.workrise.domain.model.value.YearMonthDay
import io.reyurnible.android.workrise.domain.repository.ReportRepository
import io.reyurnible.android.workrise.usecase.GetReportUseCase
import javax.inject.Inject
import kotlin.properties.Delegates

class ReportPresenter
@Inject constructor(
        private val getReportUseCase: GetReportUseCase
) {
    private var date: YearMonthDay by Delegates.notNull()
    private lateinit var view: ReportPresenter.ReportView
    private val disposableBag: CompositeDisposable = CompositeDisposable()

    fun initialize(view: ReportPresenter.ReportView, date: YearMonthDay) {
        this.date = date
        this.view = view
        getReportUseCase.get(id = ReportId(date))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ report ->
                    view.report = report
                }, { error ->
                    if (error is ReportRepository.ReportNotExistException) {
                        view.report = null
                    } else {
                        view.showErrorDialog(error)
                    }
                }).addDisposableToBag(disposableBag)
    }

    fun release() {
        disposableBag.clear()
    }

    interface ReportView {
        var report: Report?
        fun showErrorDialog(error: Throwable)
    }

}
