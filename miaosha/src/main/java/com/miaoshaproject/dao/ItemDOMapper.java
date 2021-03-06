package com.miaoshaproject.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.miaoshaproject.dataobject.ItemDO;

public interface ItemDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Thu Jan 31 04:09:39 CST 2019
     */
	List<ItemDO> listItem();
	
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Thu Jan 31 04:09:39 CST 2019
     */
    int insert(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Thu Jan 31 04:09:39 CST 2019
     */
    int insertSelective(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Thu Jan 31 04:09:39 CST 2019
     */
    ItemDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Thu Jan 31 04:09:39 CST 2019
     */
    int updateByPrimaryKeySelective(ItemDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table item
     *
     * @mbg.generated Thu Jan 31 04:09:39 CST 2019
     */
    int updateByPrimaryKey(ItemDO record);
    int increaseSales(@Param("id")Integer id,@Param("amount")Integer amount);
}