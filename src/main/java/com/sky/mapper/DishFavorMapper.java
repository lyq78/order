package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFavorMapper {

    //TODO 批量查插入 不太懂foreach
    void insertBatch(List<DishFlavor> favors);

    @Delete("delete from setmeal_dish where dish_id = #{id}")
    void delete(Long id);

    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> getById(Long id);
}
