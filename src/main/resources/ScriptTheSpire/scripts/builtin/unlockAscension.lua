addListener('PostInitialize', function(_)
    local arrayList = java.bind('java.util.ArrayList')
    local unlockTracker = java.bind('com.megacrit.cardcrawl.unlock.UnlockTracker')
    local cardCrawl = java.bind('com.megacrit.cardcrawl.core.CardCrawlGame')
    local cardLibrary = java.bind('com.megacrit.cardcrawl.helpers.CardLibrary')
    local relicLibrary = java.bind('com.megacrit.cardcrawl.helpers.RelicLibrary')
    local saveHelper = java.bind('com.megacrit.cardcrawl.helpers.SaveHelper')

    -- unlock ending
    local heart_key = 'THE_ENDING'
    if not unlockTracker.achievementPref:getBoolean(heart_key, false) then
        unlockTracker.achievementPref:putBoolean(heart_key, true)
        unlockTracker.achievementPref:flush()
    end
    cardCrawl.playerPref:putBoolean('IRONCLAD_WIN', true)
    cardCrawl.playerPref:putBoolean('THE_SILENT_WIN', true)
    cardCrawl.playerPref:putBoolean('DEFECT_WIN', true)
    cardCrawl.playerPref:flush()

    -- unlock daily
    local data = saveHelper:getPrefs('STSDataVagabond')
    data:putInteger('BOSS_KILL', 1)
    data:flush()

    -- unlock cards
    local cards = cardLibrary:getAllCards()
    for i = 0, cards:size() - 1 do
        local c = cards:get(i)
        local id = c.cardID
        unlockTracker.unlockPref:putInteger(id, 2)
        unlockTracker.lockedCards:remove(id)
        if not c.isSeen then
            c.isSeen = true
            print(c.isSeen)
            c:unlock()
            unlockTracker.seenPref:putInteger(id, 1)
        end
    end
    unlockTracker.unlockPref:flush()
    unlockTracker.seenPref:flush()

    -- unlock relics
    for i = 0, unlockTracker.lockedRelics:size() - 1 do
        unlockTracker:hardUnlockOverride(unlockTracker.lockedRelics:get(i))
    end
    unlockTracker.lockedRelics:clear()
    local relics = arrayList.new()
    relics:add(relicLibrary.starterList)
    relics:add(relicLibrary.commonList)
    relics:add(relicLibrary.uncommonList)
    relics:add(relicLibrary.rareList)
    relics:add(relicLibrary.bossList)
    relics:add(relicLibrary.specialList)
    relics:add(relicLibrary.shopList)
    for i = 0, relics:size() - 1 do
        local list = relics:get(i)
        for j = 0, list:size() - 1 do
            local r = list:get(j)
            if not r.isSeen then
                unlockTracker:markRelicAsSeen(r.relicId)
            end
        end
    end

    -- unlock ascension
    unlockTracker:hardUnlockOverride('The Silent')
    unlockTracker:hardUnlockOverride('Defect')
    unlockTracker:hardUnlockOverride('Watcher')
    unlockTracker:hardUnlockOverride('ASCEND_0')
    local prefs = cardCrawl.characterManager:getAllPrefs()
    for i = 0, prefs:size() - 1 do
        local p = prefs:get(i)
        if p:getInteger('WIN_COUNT', 0) <= 0 then
            p:putInteger('WIN_COUNT')
        end
        p:putInteger('ASCENSION_LEVEL', 20)
        p:putInteger('LAST_ASCENSION_LEVEL', 20)
        p:flush()
    end
    unlockTracker:markBossAsSeen('CROW')
    unlockTracker:markBossAsSeen('DONUT')
    unlockTracker:markBossAsSeen('WIZARD')
end)