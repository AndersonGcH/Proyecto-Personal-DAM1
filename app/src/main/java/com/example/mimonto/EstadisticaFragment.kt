package com.example.mimonto

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.mimonto.data.DBHelper
import com.example.mimonto.data.SessionManager
import com.example.mimonto.databinding.FragmentEstadisticaBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch

class EstadisticaFragment : Fragment() {
    private var _binding: FragmentEstadisticaBinding? = null
    private val binding get() = _binding!!
    private lateinit var db: DBHelper
    private lateinit var sessionManager: SessionManager

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
        sessionManager = SessionManager(requireContext())

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
            val userId = sessionManager.getUserId()
            if (userId != -1) {
                val gastosPorCategoria = db.transaccionDao().obtenerGastosAgrupadosPorCategoria(userId)

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}