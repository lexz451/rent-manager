package cu.lexz451.rentmanager.data.model

import java.io.Serializable

data class PaymentDetails (
    val forRoom: Double,
    val forProducts: Double,
    val total: Double,
    val paid: Double,
    val paidReturn: Double,
    val extraHours: Int
) : Serializable