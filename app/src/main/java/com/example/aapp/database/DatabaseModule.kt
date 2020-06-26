package com.example.aapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aapp.model.FreshDao
import com.example.aapp.model.FreshData
import com.example.aapp.model.SaveItem

/* 데이터베이스 클래스 선언 */
@Database(entities = [FreshData::class, SaveItem::class], version = 1)
abstract class DatabaseModule : RoomDatabase() {

    /* Query 문에 사용하는 FreshDao를 가져옵니다. */
    abstract fun freshDao(): FreshDao

    /* Companin Onject는 클래스의 static한 데이터를 선언하는 영역으로 싱글톤으로 사용할
       인스턴스를 메모리에 저장하거나 전역으로 이용할 변수등을 선언.*/
    companion object {
        // database 변수 선언
        private var database: DatabaseModule? = null

        //database 이름 상수 선언
        private const val ROOM_DB = "room.db"

        /* 정의한 Database 객체를 가져오는 함수 선언 */
        fun getDatabase(context: Context): DatabaseModule {
            if (database == null) {
                database = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseModule::class.java, ROOM_DB
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
            }
            /* 안전한 강제 캐스팅 */
            return database!!
        }
    }
}
