package com.laputa.arouter.service.exception

/**
 * Author by xpl, Date on 2021/4/22.
 */
class TokenException : Exception("token is invalid.") // 必须有msg，不然源码中设置tag为空，就出bug了