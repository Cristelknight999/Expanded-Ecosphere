execute as @e[type=boat,predicate=wythers:in_ocean] store result score @s[predicate=wythers:in_ocean] x run data get entity @s Pos[0] 100

scoreboard players add @e[type=boat,predicate=wythers:in_ocean] x 5

execute as @e[type=boat,predicate=wythers:in_ocean] store result entity @s Pos[0] double 0.01 run scoreboard players get @s x