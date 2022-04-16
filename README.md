# MyCache
学习Redis的思想，开发一个单机版本的缓存框架<br>
实现了单机缓存框架，进行了详细的注释。<br>
学习、修改并拓展了相关内容。<br>
灵感来源https://mp.weixin.qq.com/s/6J2K2k4Db_20eGU6xGYVTw
# 修改及拓展内容
* 淘汰算法的LRU实现中，由于没有将淘汰掉的元素从维护访问顺序的list中删除，可能会导致接下来的访问中对map中保存内容的淘汰未命中，导致报出超限错误。
	* 解决方法：将LRU实现中维护访问先后的list进行清除，测试用例已经加入
* 淘汰算法的LRU2Q实现中，由于没有将淘汰掉的元素从维护访问顺序的list中删除，可能会导致接下来的访问中对map中保存内容的淘汰未命中，导致报出超限错误。
  * 解决方法：将LRU实现中维护访问先后的list进行清除，测试用例已经加入
