import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.repo.ShopifyRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val shopifyRepo: ShopifyRepo,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Product>>(emptyList())
    val favorites: StateFlow<List<Product>> = _favorites

    fun getAllFavorites() {
        viewModelScope.launch {
            try {
                val shopifyCustomerId = sharedPreferences.getString("shopifyCustomerId", null)
                Log.d("FavoriteViewModel", "Retrieved ShopifyCustomerId from SharedPreferences: $shopifyCustomerId")
                if (shopifyCustomerId != null) {
                    val favoriteProducts = shopifyRepo.getAllFavorites(shopifyCustomerId)
                    _favorites.value = favoriteProducts
                    Log.d("FavoriteViewModel", "Retrieved ${favoriteProducts.size} favorite products")
                } else {
                    Log.e("FavoriteViewModel", "ShopifyCustomerId is null when retrieving favorites")
                }
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error retrieving favorites", e)
            }
        }
    }



    fun removeFavorite(product: Product) {
        viewModelScope.launch {
            shopifyRepo.removeFavorite(product)
            getAllFavorites()
        }
    }
}
