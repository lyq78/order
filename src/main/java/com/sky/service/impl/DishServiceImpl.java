package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFavorMapper dishFavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional//开启事务
    public void saveWithFavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //TODO aop编程
        dish.setUpdateTime(LocalDateTime.now());
        dish.setCreateTime(LocalDateTime.now());

        dish.setCreateUser(BaseContext.getCurrentId());
        dish.setUpdateUser(BaseContext.getCurrentId());
        //向菜品表插入1条数据
        dishMapper.insert(dish);

        List<DishFlavor> favors = dishDTO.getFlavors();
        if(favors != null && favors.size()>0){

            favors.forEach(dishFavor -> {
                dishFavor.setDishId(dish.getId());
            });
            //向口味表插入n条数据
            dishFavorMapper.insertBatch(favors);
        }


    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.select(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     *菜品批量删除
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断菜品状态，起售中的不能删除
        for(Long id : ids){
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断菜品是否在套餐中，在套餐中的不能删除
        for(Long id : ids){
            SetmealDish setmealDish = setmealDishMapper.getByDishId(id);
            if(setmealDish != null){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }

        //TODO 删除for循环，直接采用批量删除的sql
        for (Long id : ids) {
            //根据id删除菜品dish表
            dishMapper.delete(id);
            //根据id删除口味表
            dishFavorMapper.delete(id);
        }



    }

    @Override
    public DishVO getById(Long id) {
        //根据id查dish表
        Dish dish = dishMapper.getById(id);
        //根据id查dishflavor表
        List<DishFlavor> dishFlavor = dishFavorMapper.getById(id);

        //属性传递给dishvo
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavor);
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        dish.setUpdateTime(LocalDateTime.now());
        dish.setUpdateUser(BaseContext.getCurrentId());

        //修改dish表
        dishMapper.update(dish);

        //删除原有口味
        dishFavorMapper.delete(dishDTO.getId());

        //新增口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size()>0){

            flavors.forEach(dishFlavor ->{
                dishFlavor.setDishId(dishDTO.getId());
            });

            dishFavorMapper.insertBatch(flavors);
        }


    }
}
