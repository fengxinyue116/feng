package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")


@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name)
    {
        log.info("page{},pagesize{},name{}",page,pageSize,name);
        Page<Setmeal> page1=new Page(page,pageSize);
        Page<Setmeal> setmealDtoPage=new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();
        //过滤条件
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //执行分页查询
        setmealService.page(page1,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(page1,setmealDtoPage,"records");

        List<Setmeal> records = page1.getRecords();
        List<Setmeal> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            Long categoryId=item.getCategoryId();
            Category category=categoryService.getById(categoryId);
            if (category!=null){
                String categoryName=category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return  setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
       setmealService.removeWithDish(ids);
        //categoryService.removeById(id);

        return R.success("删除套餐成功");
    }



    @PostMapping("/status/0{id}")
    public R<String>setStatuszero(@RequestParam("ids") Long id ){
        Setmeal setmeal=setmealService.getById(id);
        setmeal.setStatus(0);
        setmealService.updateById(setmeal);
        return R.success("套餐开售成功");
    }

    @PostMapping("/status/1{id}")
    public R<String>setStatusone(@RequestParam("ids") Long id ){
        log.info(id.toString());
        Setmeal setmeal=setmealService.getById(id);
        setmeal.setStatus(1);
        setmealService.updateById(setmeal);
        return R.success("套餐停售成功");
    }

    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }



}
