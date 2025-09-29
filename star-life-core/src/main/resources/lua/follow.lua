local key = KEYS[1]
local fanId = ARGV[1]
local expireTime = tonumber(ARGV[2])  -- 转换为数字
local isFollow = ARGV[3]

if isFollow == "true" then
    -- 添加操作
    if redis.call("SISMEMBER", key, fanId) == 1 then
        return 0  -- 已存在，操作非法
    else
        redis.call("SADD", key, fanId)
        -- 设置过期时间
        redis.call("EXPIRE", key, expireTime)
        return 1  -- 添加成功
    end
elseif isFollow == "false" then
    -- 删除操作
    if redis.call("SISMEMBER", key, fanId) == 0 then
        return 0  -- 不存在，操作非法
    else
        redis.call("SREM", key, fanId)
        -- 检查集合是否为空
        if redis.call("SCARD", key) == 0 then
            -- 集合为空则删除整个key
            redis.call("DEL", key)
        else
            -- 集合不为空则更新过期时间
            redis.call("EXPIRE", key, expireTime)
        end
        return 1  -- 删除成功
    end
else
    return 0  -- 无效的操作类型
end