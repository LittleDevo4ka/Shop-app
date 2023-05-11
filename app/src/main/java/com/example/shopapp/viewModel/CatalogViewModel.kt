package com.example.shopapp.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class CatalogViewModel: ViewModel() {

    private val categoryList = mutableListOf<Category>()
    private val productList = mutableListOf<Product>()

    private val categoryFlow: MutableStateFlow<MutableList<Category>> = MutableStateFlow(categoryList)
    private val productFlow: MutableStateFlow<List<Product>> = MutableStateFlow(productList)

    private val fragmentNum: MutableStateFlow<Int> = MutableStateFlow(0)

    data class Category(
        val name: String,
        val image: String
    )
    data class Product(
        val name: String,
        val price: Double,
        val description: String,
        val image: String
    )

    init {
        val shoesCategory = Category("shoes",
            "https://assets.ajio.com/medias/sys_master/root/20211224/1tuJ/61c4c229aeb26901101a2a6a/-473Wx593H-469034008-black-MODEL.jpg")
        val smartphonesCategory = Category("smartphones",
            "https://3dnews.ru/assets/external/illustrations/2021/09/23/1049741/54545.jpg")
        val drinksCategory = Category("drinks",
            "https://ukkebabsfrome.co.uk/wp-content/uploads/2022/03/HERO_Worlds_Best_Soda_Bundaberg_shutterstock_679079920.jpeg")

        categoryFlow.value.add(shoesCategory)
        categoryFlow.value.add(smartphonesCategory)
        categoryFlow.value.add(drinksCategory)

        val nike = Product("nike blazer", 12000.0, "",
            "https://nike-rus.com/image/cache/catalog/2021/airfors/58619041-1340x1000.jpg")
        val pixel3 = Product("pixel 3", 20000.0, "",
            "https://www.droid-life.com/wp-content/uploads/2018/10/Google-Pixel-3-Review-15-1200x1200-cropped.jpg")
        val cocaWithoutSugar = Product("coca-cola without sugar", 50.0, "",
            "https://meat-grinder.ru/uploads/images/catalog/coca-cola-without-sugar-05.jpg")

        productList.add(nike)
        productList.add(pixel3)
        productList.add(cocaWithoutSugar)
    }

    fun getFragmnetNum(): MutableStateFlow<Int> {
        return fragmentNum
    }

    fun setFragmentNum(value: Int) {
        fragmentNum.value = value
    }

    fun getCategoryList(): MutableStateFlow<MutableList<Category>> {
        return categoryFlow
    }
}