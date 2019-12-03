package com.bgd

import java.io.InputStreamReader
import java.util.Properties

/**
  *
  * @author dengyu
  * @data 2019/12/3 - 16:39
  */
object PropertiesUtil {

  def load(propertieName:String): Properties ={
    val prop=new Properties();
    prop.load(new InputStreamReader(Thread.currentThread().getContextClassLoader.getResourceAsStream(propertieName) , "UTF-8"))
    prop
  }


}
