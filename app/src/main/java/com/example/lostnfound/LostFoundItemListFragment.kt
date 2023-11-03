package com.example.lostnfound

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LostFoundItemListFragment : Fragment() {

    private companion object {
        private const val SAVED_SUBTITLE_VISIBLE = "subtitle"
    }

    private lateinit var mItemRecyclerView: RecyclerView
    private var mAdapter: ItemAdapter? = null
    private var mSubtitleVisible = false

    private val cardColors = arrayOf(
        Color.parseColor("#b3c6ff"), // Blue
        Color.parseColor("#e6def6"),  // Grey
        Color.parseColor("#b3ffcc") // Creme
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lostfounditem_list, container, false)

        mItemRecyclerView = view.findViewById(R.id.item_recycler_view)
        mItemRecyclerView.layoutManager = LinearLayoutManager(activity)

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE)
        }

        updateUI()

        return view
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_item_list, menu)

        val subtitleItem = menu.findItem(R.id.show_subtitle)
        subtitleItem.title = if (mSubtitleVisible) "Hide Items" else "Show Items"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_item -> {
                val lostFoundItem = LostFoundItem()
                LostFoundItemLab.get(requireActivity()).addItem(lostFoundItem)
                val intent =
                    LostFoundItemPagerActivity.newIntent(requireActivity(), lostFoundItem.mId)
                startActivity(intent)
                true
            }

            R.id.show_subtitle -> {
                mSubtitleVisible = !mSubtitleVisible
                requireActivity().invalidateOptionsMenu()
                updateSubtitle()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateSubtitle() {
        val lostFoundItemLab = LostFoundItemLab.get(requireActivity())
        val itemCount = lostFoundItemLab.getItems().size
        val subtitle =
            resources.getQuantityString(R.plurals.item_subtitle_plural, itemCount, itemCount)

        (requireActivity() as AppCompatActivity).supportActionBar?.subtitle =
            if (mSubtitleVisible) subtitle else null
    }

    private fun updateUI() {
        val itemLab = LostFoundItemLab.get(requireActivity())
        val items = itemLab.getItems()

        if (mAdapter == null) {
            mAdapter = ItemAdapter(items)
            mItemRecyclerView.adapter = mAdapter
        } else {
            mAdapter?.setItems(items)
            mAdapter?.notifyDataSetChanged()
        }
        updateSubtitle()
    }

    private inner class LostFoundItemHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        private lateinit var mItem: LostFoundItem
        private val mTitleTextView: TextView = itemView.findViewById(R.id.item_title)
        private val mDateTextView: TextView = itemView.findViewById(R.id.lost_date)
        private val mTimeTextView: TextView = itemView.findViewById(R.id.lost_time)
        private val mSolvedImageView: ImageView = itemView.findViewById(R.id.item_found)
        val cardView: CardView = itemView.findViewById(R.id.card_view)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(item: LostFoundItem) {
            mItem = item
            mTitleTextView.text = mItem.mTitle
            val df: DateFormat = SimpleDateFormat("E, MMMM dd, yyyy")
            mDateTextView.text = df.format(mItem.mDate)
            val tf: DateFormat = SimpleDateFormat("hh:mm a")
            mTimeTextView.text = tf.format(mItem.mTime)
            mSolvedImageView.visibility = if (mItem.mFound) View.VISIBLE else View.GONE

            val randomColor = cardColors[Random().nextInt(cardColors.size)]
            cardView.setCardBackgroundColor(randomColor)
        }

        override fun onClick(view: View) {
            val intent = LostFoundItemPagerActivity.newIntent(requireActivity(), mItem.mId)
            startActivity(intent)
        }
    }

    private inner class ItemAdapter(private var mItems: List<LostFoundItem>) :
        RecyclerView.Adapter<LostFoundItemHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LostFoundItemHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_lostfound, parent, false)
            return LostFoundItemHolder(view)
        }

        override fun onBindViewHolder(holder: LostFoundItemHolder, position: Int) {
            val item = mItems[position]
            holder.bind(item)

            val sequencePosition = position % cardColors.size
            val color = cardColors[sequencePosition]
            holder.cardView.setCardBackgroundColor(color)
        }

        override fun getItemCount(): Int {
            return mItems.size
        }

        fun setItems(items: List<LostFoundItem>) {
            mItems = items
        }
    }
}
