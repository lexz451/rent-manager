package cu.lexz451.rentmanager.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import cu.lexz451.rentmanager.databinding.FragmentReportDetailBinding

class ReportDetailFragment : Fragment() {

    private var binding: FragmentReportDetailBinding? = null
    private val args by navArgs<ReportDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.html = args.report.generateHTMLReport()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}