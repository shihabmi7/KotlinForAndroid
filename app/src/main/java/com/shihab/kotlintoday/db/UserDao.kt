package com.shihab.kotlintoday.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shihab.kotlintoday.db.entity.CURRENT_USER_ID
import com.shihab.kotlintoday.db.entity.User

@Dao
interface UserDao{

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun upsert(user: User) : Long


    @Query("SELECT * FROM User WHERE uid = $CURRENT_USER_ID")
    fun getUser(): LiveData<User>
}