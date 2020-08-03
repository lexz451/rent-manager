package cu.lexz451.rentmanager.ui.reports

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cu.lexz451.rentmanager.ManagerApp

import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Report
import cu.lexz451.rentmanager.utils.ReportDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelFactory
import cu.lexz451.rentmanager.vm.ReportListViewModel
import kotlinx.android.synthetic.main.fragment_report_list.*
import kotlinx.android.synthetic.main.report_list_item.view.*
import net.ozaydin.serkan.easy_csv.EasyCsv
import net.ozaydin.serkan.easy_csv.FileCallback
import org.dizitart.kno2.filters.and
import org.dizitart.no2.objects.filters.ObjectFilters
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
class ReportListFragment : Fragment() {

    private val viewModel by viewModels<ReportListViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = Adapter()
        reportList.adapter = adapter
        viewModel.reports.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        btnExport.setOnClickListener {
            // Export data
           // exportData()
            exportGeneralData()
        }
    }


    private fun exportGeneralData() {
        val fileName = "reporte-general-${LocalDate.now().format(DateTimeFormatter.ofPattern("d-MM-YY"))}"
        val csv = EasyCsv(requireActivity())
        val headerList = arrayListOf("Fecha.Turno.Habitaciones.Productos.Importe-")
        val dataList = arrayListOf<String>()
        viewModel.reports.value?.let { reports ->
            reports.forEach { rep ->
                val date = rep.date.format(DateTimeFormatter.ofPattern("d/MM/YY"))
                val shift = rep.shift.employee
                val rooms = rep.clients.count()
                val products = rep.clients.sumBy { it.products.count() }
                val totalForRooms = rep.clients.sumByDouble { it.paymentDetails.forRoom }
                val totalForProducts = rep.clients.sumByDouble { it.paymentDetails.forProducts }
                val total = totalForProducts + totalForRooms
                dataList.add("${date}.${shift}.${rooms}.${products}.${total}-")
            }
        }
        csv.setSeparatorColumn(".")
        csv.setSeperatorLine("-")
        csv.createCsvFile(fileName, headerList, dataList, REQUEST_WRITE_STORAGE, object : FileCallback {
            override fun onSuccess(p0: File?) {
                Log.d("ManagerApp", "Success")
                Toast.makeText(requireContext(), "Datos exportados con exito", Toast.LENGTH_LONG).show()
            }

            override fun onFail(p0: String?) {
                Log.e("ManagerApp", p0 ?: "")
            }
        })
    }

    inner class ReportViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {
        fun bind(report: Report) {
            view.shift.text = report.shift.employee
            view.date.text = report.date.format(DateTimeFormatter.ofPattern("d/MM/YY"))
            val products = report.clients.sumBy { client ->
                client.products.sumBy { p -> p.quantity }
            }

            val vipRecord = ManagerApp.instance.database.vipStore.find(
                ObjectFilters.eq("shift.__id", report.shift.__id)
                    .and(ObjectFilters.eq("date", report.date.toString()))
            ).firstOrNull()

            val vipProducts = vipRecord?.products?.sumBy { it.quantity } ?: 0

            val totalProducts = products + vipProducts

            view.totalProducts.text = totalProducts.toString()
            view.totalClients.text = report.clients.size.toString()
            view.totalPay.text = report.clients.sumByDouble { client ->
                client.paymentDetails.total
            }.toString()

            view.btnRemove.setOnClickListener {
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.warning)
                    .setMessage(R.string.confirm_delete_report)
                    .setPositiveButton(R.string.accept) { dialog, _ ->
                        viewModel.removeReport(report)
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                dialog.create()
                dialog.show()
            }

            view.rootView.setOnClickListener {
                findNavController().navigate(ReportListFragmentDirections
                    .actionReportListFragmentToReportDetailFragment(report))
            }
        }
    }

    inner class Adapter : ListAdapter<Report, RecyclerView.ViewHolder>(ReportDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.report_list_item, parent, false)
            return ReportViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ReportViewHolder).bind(getItem(position))
        }
    }

    companion object {
        const val REQUEST_WRITE_STORAGE = 112
    }

}
