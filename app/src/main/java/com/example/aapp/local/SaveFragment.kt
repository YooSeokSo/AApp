package com.example.aapp.local

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aapp.R
import com.example.aapp.database.DatabaseModule
import com.example.aapp.model.FreshData
import com.example.aapp.result.SaveAdpater
import kotlinx.android.synthetic.main.fragment_save.view.*

//DB에 저장된 검색 결과를 클릭하면 리사이클러뷰에 표시
class SaveFragment : Fragment() {

    /* 데이터베이스를 가져옵니다.*/
    val database by lazy {
        /* DatabaseModule.getDatabase(싱글톤)를 이용하여 데이터베이스를 가져옵니다.*/
        DatabaseModule.getDatabase(requireContext())
    }

    //saveAdapter 생성
    val saveAdapter by lazy { SaveAdpater() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //ragment_save 뷰 inflate
        return inflater.inflate(R.layout.fragment_save, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //리사이클러뷰에 어댑터 및 LayoutManager 설정
        view.list_save.adapter = saveAdapter
        view.list_save.layoutManager = LinearLayoutManager(requireContext())

        /* 전달받은 bundle 객체에서 SAVE_ID를 추출  */
        arguments?.getLong("SAVE_ID")?.let { saveId ->
            //저장된 DB에서 saveId로 검색하여  출력

            /* PagedList 를 LiveData 형태로 사용하기 위해서 LivePagedListBuilder 를 통해서
               LiveData<PagedList<Type>> 타입으로 생성 */
            val pageLiveData: LiveData<PagedList<FreshData>> = LivePagedListBuilder(
                            database.freshDao().loadFreshData(saveId = saveId),20).build()

            //LiveData 형태인 PagedList를 observe
            pageLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                //saveAdapter(PagedListAdapter)에 pageList를 리사이클러뷰에 바인딩 해줄 것을 요청
                saveAdapter.submitList(it)
            })
        }
    }//end of onViewCreated
}

