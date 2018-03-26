package me.framework.test.mappers;

import me.framework.test.DemoModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 */
public interface DemoMapper {

    List<DemoModel> getDiscount(@Param("id") long id);

}
