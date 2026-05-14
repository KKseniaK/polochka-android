package com.hse.polochka.feature.recipes.presentation.adapter

object RecipeTagFormatter {
    fun readable(tag: String): String =
        when (tag) {
            "gluten_free" -> "без глютена"
            "lactose_free" -> "без лактозы"
            "vegan" -> "веганское"
            "vegetarian" -> "вегетарианское"
            "protein" -> "белковое"
            "diet" -> "легкое"
            "fatty" -> "жирное"
            "spicy" -> "острое"
            "sugar_free" -> "без сахара"
            "halal" -> "халяль"
            "milk" -> "молочное"
            "sour_milk" -> "кисломолочное"
            "cheese" -> "сыр"
            "meat" -> "мясо"
            "fish" -> "рыба"
            "poultry" -> "птица"
            "seafood" -> "морепродукты"
            "sausage" -> "колбасное"
            "vegetables" -> "овощи"
            "fruits" -> "фрукты"
            "greens" -> "зелень"
            "berries" -> "ягоды"
            "mushrooms" -> "грибы"
            "bakery" -> "выпечка"
            "pasta" -> "паста"
            "grains" -> "крупы"
            "coffee" -> "кофе"
            "tea" -> "чай"
            "sweet" -> "сладкое"
            "salty" -> "соленое"
            "hot" -> "горячее"
            else -> tag.replace('_', ' ')
        }
}
