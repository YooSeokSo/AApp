package com.example.aapp.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.aapp.R
import com.example.aapp.model.FreshDao
import com.example.aapp.model.SaveItem
import kotlinx.android.synthetic.main.list_item_saveitem.view.*

/* 시작 화면(SearchFragment)에서 저장된 데이터를 리사이클러뷰에 보여주는 어댑터
   - PagedListAdapter 상속
   - PagedListAdapter 는 PagedList 를 RecyclerView 에 효율적으로 연결하기 위한 Adapter 구현을 제공하며,
     PageList 가 업데이트 될 때마다 UI 업데이트가 적절히 갱신될 수 있도록 처리
* */
class SearchAdapter(val dao: FreshDao) :
                        PagedListAdapter<SaveItem, SearchAdapter.ItemViewHolder>(DIFF_CALLBACK) {

    /* 뷰홀더를 생성하여 반환 */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_saveitem, parent, false)
        return ItemViewHolder(rootView)
    }

    //뷰홀더에 데이터 바인딩(bindItems() 함수를 호출)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindItems(getItem(position))
        Log.d("MDIFFER", "${getItem(position)}, SearchAdapter")
        //SaveItem(id=1, saveTitle=2020-06-19 사과 검색결과)
    }

    //뷰홀더 클래스 선언
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {        
        fun bindItems(saveItem: SaveItem?) {
            /* local DB에 저장된 경락가격정보의 목록을 리사이클러뷰로 보여줄 때 아이템뷰의 텍스트를 설정
               -  SaveItem 테이블에 저장된 경락가격정보 타이틀(saveTitle)을 itemView.txt_save_subject에 설정
             */
            itemView.txt_save_subject.text = saveItem?.saveTitle

            //삭제 버튼 클릭하면 DB에서 해당 SaveItem 삭제
            itemView.btn_delete.setOnClickListener {
                saveItem?.id?.let { dao.deleteSaveData(it) }
            }

            /* 검색결과 텍스트(2020-06-15 사과 검색결과)를 클릭하면, 내비게이션을 통해 saveFragment로
               화면을 전환하여 해당 검색 결과(saveItem)를 화면에 출력
               - 해당 saveItem의 id를 "SAVE_ID"로 Bundle 객체에 저장하여 saveFragment에 전달
             */
            itemView.txt_save_subject.setOnClickListener {
                /* 액티비티를 시작합니다.*/
                Navigation.findNavController(itemView).navigate(
                    R.id.action_searchFragment_to_saveFragment,
                    Bundle().apply {
                        putLong("SAVE_ID", saveItem!!.id!!)
                    })
            }
        }
    }

    /*  DiffUtil 클래스는 전달 받은 pageList를 비교하여 갱신할 pageList를 반환
        - 기존에 PagedList 가 존재하면 그 차이를 비교한 후 리사이클러뷰를 새로운 페이지로 갱신
        - areItemsTheSame() : 두 객체가 같은 항목인지 여부를 결정
        - areContentsTheSame() : 두 항목의 데이터가 같은지 여부를 결정
     */
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SaveItem>() {
            override fun areItemsTheSame(oldConcert: SaveItem, newConcert: SaveItem): Boolean =
                oldConcert.id == newConcert.id

            override fun areContentsTheSame(oldConcert: SaveItem, newConcert: SaveItem): Boolean =
                oldConcert.id == newConcert.id
        }
    }
}