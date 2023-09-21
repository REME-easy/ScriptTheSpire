---@alias GameEvent table
---@alias EventHandler fun(event:GameEvent)

--- Add a listener to be called when the specified event is triggered.
---@see GameEventType
---@param event string event.
---@param func EventHandler callback.
function addListener(event, func)
end

java = {
    ---@param clz string
    ---@return Class
    bind = function(clz)
    end,

    ---@param clz Class
    ---@vararg any
    new = function(clz, ...)
    end,

    ---@param clz string
    ---@param func table
    proxy = function(clz, func)
    end
}

---@class Class
class = {
    ---@vararg any
    new = function(...)
    end,
}

---@class sts
sts = {
    ---@param id string
    newCard = function(id, ...)
    end,

    ---@param id string
    newRelic = function(id, ...)
    end,

    ---@param id string
    newPotion = function(id, ...)
    end,

    ---@param id string
    newPower = function(id, ...)
    end,

    ---@param func string | fun()
    newAction = function(func, ...)
    end,

    ---@param target any
    ---@param amount number
    ---@param type string | nil
    newDamageInfo = function(target, amount, type)
    end,

    addToBot = function(action)
    end,

    addToTop = function(action)
    end,
}