package cu.lexz451.rentmanager.utils

import androidx.recyclerview.widget.DiffUtil
import cu.lexz451.rentmanager.data.model.*

class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.__id == newItem.__id
    }
}

class ReportDiffCallback : DiffUtil.ItemCallback<Report>() {
    override fun areContentsTheSame(oldItem: Report, newItem: Report): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Report, newItem: Report): Boolean {
        return oldItem.__id == newItem.__id
    }
}

class ClientDiffCallback : DiffUtil.ItemCallback<Client>() {
    override fun areContentsTheSame(oldItem: Client, newItem: Client): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Client, newItem: Client): Boolean {
        return oldItem.__id == newItem.__id
    }
}

class RoomDiffCallback : DiffUtil.ItemCallback<Room>() {
    override fun areContentsTheSame(oldItem: Room, newItem: Room): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Room, newItem: Room): Boolean {
        return oldItem.__id == newItem.__id
    }
}

class ShiftDiffCallback : DiffUtil.ItemCallback<Shift>() {
    override fun areContentsTheSame(oldItem: Shift, newItem: Shift): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Shift, newItem: Shift): Boolean {
        return oldItem.__id == newItem.__id
    }
}

class PriceDiffCallback : DiffUtil.ItemCallback<Price>() {
    override fun areContentsTheSame(oldItem: Price, newItem: Price): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Price, newItem: Price): Boolean {
        return oldItem.__id == newItem.__id
    }
}

class BRecordDiffCallback : DiffUtil.ItemCallback<BlackListRecord>() {
    override fun areContentsTheSame(oldItem: BlackListRecord, newItem: BlackListRecord): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: BlackListRecord, newItem: BlackListRecord): Boolean {
        return oldItem.__id == newItem.__id
    }
}