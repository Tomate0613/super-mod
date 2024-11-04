local currentTick = 0
local left = 0

local lines = {
  "Initializing piOS v2.1.01p",
  "Disk check: okay.",
  "Memory check: okay.",
  "VR gear check: okay.",
  "Network check: okay.",
  20,
  false,
  20,
  " \n ",
  "        @@  @@@@@@  @@@@@@@ ",
  "           @@    @@ @@      ",
  " @@@@@  @@ @@    @@ @@@@@@@ ",
  "@@   @@ @@ @@    @@      @@ ",
  "@@@@@   @@  @@@@@@  @@@@@@@ ",
  "@@",
  "@@",
  "@@",
  20,
  false,
}

local function tick()
  if left > 0 then
    local currentLine = lines[currentTick]

    if currentLine ~= false then
      print(currentLine)
    end

    left = left - 1
    return
  end

  currentTick = currentTick + 1

  if currentTick > #lines then
    io.write("\027[J\027[H")
    puter.stop()

    return
  end

  local currentLine = lines[currentTick]

  if type(currentLine) == "number" then
    left = currentLine
    currentTick = currentTick + 1
    return
  end

  if currentLine ~= false then
    print(currentLine)
  end
end

puter.on("tick", tick)
