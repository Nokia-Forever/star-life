local key = KEYS[1]
local userId = ARGV[1]


-- 检查set中是否已存在该userId
if redis.call('SISMEMBER', key, userId) == 1 then
    -- 如果存在，则移除userId（取消关注）
    redis.call('SREM', key, userId)
    return 0  -- 返回0表示取消关注
else
    -- 如果不存在，则添加userId（关注）
    redis.call('SADD', key, userId)
    return 1  -- 返回1表示关注
end