package com.example.mimonto

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.mimonto.data.DBHelper
import com.example.mimonto.databinding.FragmentEstadisticaBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EstadisticaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EstadisticaFragment : Fragment() {
    private var _binding: FragmentEstadisticaBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEstadisticaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = DBHelper.getDatabase(requireContext())

        setupChart()
        cargarDatosDelGrafico()
    }
    private fun setupChart() {
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            setTransparentCircleAlpha(0)
            setDrawCenterText(true)
            centerText = "Gastos"
            setCenterTextSize(18f)
            setCenterTextColor(Color.WHITE)
            legend.textColor = Color.WHITE
            legend.textSize = 12f
            setEntryLabelColor(Color.WHITE)
        }
    }
    private fun cargarDatosDelGrafico() {
        lifecycleScope.launch {
            val gastosPorCategoria = db.transaccionDao().obtenerGastosAgrupadosPorCategoria()

            if (gastosPorCategoria.isEmpty()) {
                binding.pieChart.centerText = "No hay gastos"
                binding.pieChart.data?.clearValues()
                binding.pieChart.invalidate()
                return@launch
            }

            val entries = ArrayList<PieEntry>()
            gastosPorCategoria.forEach { gasto ->
                entries.add(PieEntry(gasto.total.toFloat(), gasto.categoria))
            }

            val dataSet = PieDataSet(entries, "").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList() + ColorTemplate.VORDIPLOM_COLORS.toList()
                sliceSpace = 2f
            }

            val data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter(binding.pieChart))
                setValueTextSize(12f)
                setValueTextColor(Color.BLACK)
            }

            binding.pieChart.data = data
            binding.pieChart.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}