用来标记目录.

-- 啊, md 赶快写上一大堆文档吧.



向上标记/向下标记

本质上都是标记.

因此合二为一, .tag 和 .register 合并为 .dirtag.



链式搜索 -c 

​	可以提高速度, 避免全盘搜索.

​	空文件 .dirtag 作为中间结点是必须的.



生成特殊索引文件, 用 %... 表示

​	%cooltask, %vcsdir



更多的功能

​	在 tag 后面加上特殊的关键字, 用空格分隔

​	比如: 系统级 lib sys, 用户级 perl user.
