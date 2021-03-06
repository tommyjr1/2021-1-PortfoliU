package com.example.app_profile.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao_dday {

    @Insert
    void insert(User_dday user);

    @Update
    void update(User_dday user);

    @Query("UPDATE Dday SET user_dyear = :t, user_dmonth = :d, user_ddate =:s  WHERE user_id =:id")
    void update(int t, int d, int s, int id);

    @Delete
    void delete(User_dday user);

    @Query("SELECT * FROM  Dday")
    List<User_dday> getAll();

    @Query("DELETE FROM Dday")
    void deleteAll();

    @Query("SELECT COUNT(*) as cnt FROM Dday")
    int getDataCount();
}
