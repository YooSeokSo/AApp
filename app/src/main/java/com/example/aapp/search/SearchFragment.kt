package com.example.aapp.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aapp.R
import com.example.aapp.database.DatabaseModule
import com.example.aapp.model.Fruits
import com.example.aapp.model.SaveItem
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import kotlinx.android.synthetic.main.fragment_search.view.btn_search
import kotlinx.android.synthetic.main.fragment_search.view.layout_date
import kotlinx.android.synthetic.main.fragment_search.view.layout_type
import kotlinx.android.synthetic.main.fragment_search.view.txt_gongpan
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {

    var selectedFruit: String? = null
    var selectedDate: String? = null

    /* 데이터베이스를 가져옵니다.*/
    val database by lazy {
        /* DatabaseModule.getDatabase(싱글톤)를 이용하여 데이터베이스를 가져옵니다.*/
        DatabaseModule.getDatabase(requireContext())
    }

    /* SearchAdapter 생성
       - SearchAdapter는 PagedListAdapter를 상속하고 있기 때문에
         내부적으로 AsyncPagedListDiffer 를 생성해서 유지
     */
    val searchAdapter by lazy { SearchAdapter(database.freshDao()) }

    /* todo2 - 사용자가 클릭할때 분류(과일)를 선택하는 Dialog 설정
       - alertDialog를 사용하여 과일을 선택하는 Dialog 설정
       - AlertDialog에 setItems(컬렉션, 리스너)를 사용하면 SingleChoice Dialog를 만들 수 있음
       - setItems(CharSequence[] items, final OnClickListener listener)
     */
    /* -----------------------Alert Dialog------------------------*/
    /* 과일 선택을 위한 Dialog 설정입니다. */
    val alertDialog by lazy {

        /* Dialog의 builder 클래스를 초기화 */
        val builder = AlertDialog.Builder(requireContext())
        /* builder에 Dialog 제목을 설정 */
        builder.setTitle("분류를 선택해주세요.")

        /* 분류 Dialog에서 과일을 선택하면, 해당 과일의 상수명을 반환하도록 builder 설정
            - map: 컬렉션 내 인자를 변환하여 반환
            - toTypedArray(): 컬렉션을 배열로 변환
            - OnClickListener를 통해 선택된 아이템의 index를 넘겨 받아, Fruits.values()[index]로
              선택한 과일의 상수명을 selectedFruit에 저장
        */
        builder.setItems(Fruits.values().map { it.holder }.toTypedArray()) { dialog, index ->
            /*  선택한 Enum의 키값(상수명)을 String 형태로 리턴해줍니다. */
            with(Fruits.values()[index]) {
                selectedFruit = this.name

                /* 선택한 과일명을 선택창에 표시 */
                text_type.text = this.holder
            }
            Log.i("FRESH", "which: $index - $selectedFruit")//0 - APPLE

            /* 검색버튼의 색상을 변경하는 함수 호출 */
            checkCondition()

            //changeInputTextBydate()
        }

        /* builder에 취소 버튼을 설정(이벤트는 Null로 설정) */
        builder.setNegativeButton("취소", null)

        /* 만든 builder를 리턴합니다. */
        builder.create() //return 되는 value
    }
    /* -----------------------Alert Dialog------------------------*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /* 리사이클러뷰의 구분선(Divider)를 만들어줍니다. */
        DividerItemDecoration(
            requireContext(),
            LinearLayoutManager(requireContext()).orientation
        ).run {
            //리사이클러뷰(list_save)에 구분선(Divider) 추가
            view.list_search.addItemDecoration(this)
        }

        /* 리사이클러뷰에 어댑터 및 레이아웃메니저 설정 */
        view.list_search.adapter = searchAdapter
        view.list_search.layoutManager = LinearLayoutManager(requireContext())


        /* todo1 - 사용자가 layout_type([분류를선택해주세요] 레이아웃)을 클릭한 경우 이벤트 처리
            - [분류를선택해주세요] 레이아웃을 클릭하면 과일을 선택하는 Dialog를 출력하는 alertDialog를 실행
        */
        layout_type.setOnClickListener { alertDialog.show() }

        //todo3 - 날짜 Dialog를 띄워줍니다.
        /*[날짜를선택해주세요] 레이아웃을 클릭했을경우 */
        layout_date.setOnClickListener {
            /*현재날짜를 가져옵니다.*/
            val currentCaldenar =
                Calendar.getInstance().apply { time = Date(System.currentTimeMillis()) }

            /* 날짜를 선택하는 다이얼로그를 만듭니다.*/
            DatePickerDialog(
                requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    currentCaldenar.apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }.run {
                        /* 선택한 데이터를 2020-01-01와 같은 형식으로 가져옵니다.*/
                        selectedDate = SimpleDateFormat("yyyy-MM-dd").format(currentCaldenar.time)
                        /* 날짜 선택 여부를 체크하여 처리하기 위한 함수 호출
                           - 선택한 날짜를 선택창에 표시 */
                        changeInputTextBydate()
                    }
                },
                /* DatePickerDialog의 Date를 오늘 날짜로 초기화)*/
                currentCaldenar.get(Calendar.YEAR),
                currentCaldenar.get(Calendar.MONTH),
                currentCaldenar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


        /*  PagedList 를 LiveData 형태로 사용하기 위해서 LivePagedListBuilder 를 통해서
            LiveData<PagedList<Type>> 타입으로 생성
            - LivePagedListBuilder(DataSource.Factory<Int, SaveItem>, pageSize).build(): PagedList 생성
            - pageSize: 한 번에 쿼리하는 데이터 사이즈(어탭더에 데이터 사이즈를 알려줘야 하기 때문에 필요)
         */
        val pageLiveData: LiveData<PagedList<SaveItem>> = LivePagedListBuilder(database.freshDao().loadSaveItems(),20).build()


        //LiveData 형태인 PagedList를 observe

        pageLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            /* searchAdapter(PagedListAdapter)에 pageList를 리사이클러뷰에 바인딩 해줄 것을 요청
               - pagedList를 인자로 submitList()를 호출하여 PagedListAdapter에 pagedList 전달
               - PagedListAdapter 는 내부적으로 AsyncPagedListDiffer 라는 객체를 가지고 있는데,
                 이는 바인딩 요청(submit) 되는 페이지와 기존에 바인딩 된 페이지의 비교를 비동기로 수행한 후
                 UI(리사이클러뷰)를 새로운 페이지로 업데이트
             */

            searchAdapter.submitList(it)
        })


        /* todo4 - 사용자가 검색 버튼(btn_search)을 클릭했을경우 이벤트 처리 */
        view.btn_search.setOnClickListener {
            /* 분류와 날짜를 선택했는지 검증
               - selectedFruit: 과일/야채 상수명(APPLE, PEAR, GRADPE, ...)
               - selectedDate:  날짜(YYYY-MM-DD)
            */
            if (selectedDate == null || selectedFruit == null) {
                /* 선택이 안된 경우 토스트 메세지 출력 */
                Toast.makeText(requireContext(), "분류와 날짜를 입력해주세요.", Toast.LENGTH_LONG).show()
            } else {
                /* 내비게이션을 통해서 resultFragment로 화면 전환
                   - 과일/야채 상수명, 검색일자, 수량을 Bundle 객체에 담아 ResultFragemtnt에 전달 */
                findNavController().navigate(
                    R.id.action_searchFragment_to_resultFragment,
                    Bundle().apply {
                        putString("SELECT_FRUIT", selectedFruit)
                        putString("SELECT_DATE", selectedDate)
                        putString("RESULT_AMOUNT", view.findViewById<RadioButton>(view.radio_layout.checkedRadioButtonId).tag.toString())
                    })
                selectedDate = null
                selectedFruit = null
            }
        }//end of view.btn_search.setOnClickListener
    }//end of onViewCreated

    /* 날짜 선택 여부를 체크하여 선택한 날짜를 선택창에 표시하는 함수*/
    private fun changeInputTextBydate() {
        /* 분류와 날짜를 모두 선택하면 검색버튼의 색상을 변경하는 함수호출 */
        checkCondition()
        /*Date이 선택되었다면 날짜 선택창에 날짜 표시*/
        selectedDate?.let { txt_gongpan.text = it }
    }

    /* 분류와 날짜를 모두 선택하면 검색버튼과 글자의 색상을 변경하는 함수 */
    private fun checkCondition() {
        if (selectedDate != null && selectedFruit != null) {
            btn_search.setBackgroundColor(resources.getColor(android.R.color.holo_green_dark))
            btn_search.setTextColor(resources.getColor(android.R.color.white))
        }
    }
}
