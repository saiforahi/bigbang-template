package com.xmartlabs.template.ui.users

import android.arch.paging.PagedListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.xmartlabs.template.service.NetworkState
import com.xmartlabs.template.R
import com.xmartlabs.template.model.User

class ListUsersAdapter(private val retryCallback: () -> Unit)
  : PagedListAdapter<User, RecyclerView.ViewHolder>(POST_COMPARATOR) {
  private var networkState: NetworkState? = null
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (getItemViewType(position)) {
      //R.layout.item_user -> (holder as UserViewHolder).bind(getItem(position))
      R.layout.network_state_item -> (holder as NetworkStateItemViewHolder).bindTo(
          networkState)
    }
  }

  override fun onBindViewHolder(
      holder: RecyclerView.ViewHolder,
      position: Int,
      payloads: MutableList<Any>) {
    if (payloads.isNotEmpty()) {
      val item = getItem(position)
      //(holder as UserViewHolder).updateScore(item)
    } else {
      onBindViewHolder(holder, position)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      R.layout.item_user -> UserViewHolder(parent)
      R.layout.network_state_item -> NetworkStateItemViewHolder.create(parent, retryCallback)
      else -> throw IllegalArgumentException("unknown view type $viewType")
    }
  }

  private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

  override fun getItemViewType(position: Int): Int {
    return if (hasExtraRow() && position == itemCount - 1) {
      R.layout.network_state_item
    } else {
      R.layout.item_user
    }
  }

  override fun getItemCount(): Int {
    return super.getItemCount() + if (hasExtraRow()) 1 else 0
  }

  fun setNetworkState(newNetworkState: NetworkState?) {
    val previousState = this.networkState
    val hadExtraRow = hasExtraRow()
    this.networkState = newNetworkState
    val hasExtraRow = hasExtraRow()
    if (hadExtraRow != hasExtraRow) {
      if (hadExtraRow) {
        notifyItemRemoved(super.getItemCount())
      } else {
        notifyItemInserted(super.getItemCount())
      }
    } else if (hasExtraRow && previousState != newNetworkState) {
      notifyItemChanged(itemCount - 1)
    }
  }

  companion object {
    val POST_COMPARATOR = object : DiffUtil.ItemCallback<User>() {
      override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
          oldItem == newItem

      override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
          oldItem.name == newItem.name
    }
  }

  class UserViewHolder(view: View)
    : RecyclerView.ViewHolder(view) {

  }
}
