package at.technikumwien.maps.data.local;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import at.technikumwien.maps.data.model.DrinkingFountain;

/**
 * Created by Bellacraine on 04.11.2017.
 */

public interface DrinkingFountainDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<DrinkingFountain> drinkingFountains);

    @Query("DELETE FROM " + DrinkingFountain.TABLE_NAME)
    void deleteAll();

    @Query("SELECT * FROM " + DrinkingFountain.TABLE_NAME)
    List<DrinkingFountain> loadAll();

}
