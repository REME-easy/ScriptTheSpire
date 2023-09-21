---@alias GameEvent table
---@alias EventHandler fun(event:GameEvent)

--- Add a listener to be called when the specified event is triggered.
---@see GameEventType
---@param event string event.
---@param func EventHandler callback.
function addListener(event, func)
end

--- Add a command that can be executed in console.
---@param name string
---@param func fun(builder: CommandBuilder):fun(...)
function addCommand(name, func)
end

---@class CommandBuilder
---@field addArg fun(name: string, type: string, default: any)
local cmd_builder = {}

---@param id string
---@param func fun(card: GeneratedCard)
function addCard(id, func)
end

---@class GeneratedCard
---@field cost number
---@field baseDamage number
---@field baseBlock number
---@field baseMagicNumber number
---@field damage number
---@field block number
---@field magicNumber number
---@field exhaust boolean
---@field shuffleBackIntoDrawPile boolean
---@field selfRetain boolean
---@field isEthereal boolean
local card = Class()

---setLocale
---@param name string
---@param desc string
---@param upgradeDesc string
---@param extended string[]
function card:setLocale(name, desc, upgradeDesc, extended)
end

---setCardType
---@param type string
function card:setCardType(type)
end

---setCardColor
---@param color string
function card:setCardColor(color)
end

---setCardRarity
---@param rarity string
function card:setCardRarity(rarity)
end

---setCardTarget
---@param target string
function card:setCardTarget(target)
end

---setOnUpgrade
---@param func fun(self: GeneratedCard)
function card:setOnUpgrade(func)
end

---setOnUse
---@param func fun(self: GeneratedCard, player, monster)
function card:setOnUse(func)
end

---setOnTookDamage
---@param func fun(self: GeneratedCard)
function card:setOnTookDamage(func)
end

---setOnDrawn
---@param func fun(self: GeneratedCard)
function card:setOnDrawn(func)
end

---setOnManualDiscard
---@param func fun(self: GeneratedCard)
function card:setOnManualDiscard(func)
end

---setOnRetained
---@param func fun(self: GeneratedCard)
function card:setOnRetained(func)
end

---setOnExhausted
---@param func fun(self: GeneratedCard)
function card:setOnExhausted(func)
end

---setOnScry
---@param func fun(self: GeneratedCard)
function card:setOnScry(func)
end

---setOnApplyPower
---@param func fun(self: GeneratedCard)
function card:setOnApplyPower(func)
end

--- Tools useful for console dev.
env = {
    ---@param label string
    ---@param default number
    ---@return number
    requireInt = function(label, default)
    end
}

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
---@field dungeon table
---@field player table
sts = {
    dungeon = {},

    player = {},

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