local stockKey = KEYS[1]
local orderKey = KEYS[2]
local userId = ARGV[1]

-- 处理key不存在的情况
local stock = redis.call("GET", stockKey)
if not stock then
    return 3 -- 新增错误码：库存未初始化
end

-- 转换并检查库存
local stockNum = tonumber(stock)
if stockNum <= 0 then
    return 1
end

-- 检查用户是否下单
if redis.call("SISMEMBER", orderKey, userId) == 1 then
    return 2
end

--库存减一
redis.call("incrby", stockKey, -1)
--添加进set中
redis.call("sadd", orderKey, userId)
return 0