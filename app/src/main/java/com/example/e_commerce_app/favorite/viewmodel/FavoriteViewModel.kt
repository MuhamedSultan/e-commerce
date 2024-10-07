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
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Product>>(emptyList())
    val favorites: StateFlow<List<Product>> = _favorites

    fun getAllFavorites(shopifyCustomerId: String) {
        viewModelScope.launch {
            try {
                val favoriteProducts = shopifyRepo.getAllFavorites(shopifyCustomerId)
                _favorites.value = favoriteProducts
                Log.d("FavoriteViewModel", "Retrieved ${favoriteProducts.size} favorite products")
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error retrieving favorites", e)
            }
        }
    }


    fun removeFavorite(product: Product, shopifyCustomerId: String) {
        viewModelScope.launch {
            try {
                shopifyRepo.removeFavorite(product)
                getAllFavorites(shopifyCustomerId)
            } catch (e: Exception) {
                Log.e("FavoriteViewModel", "Error removing favorite", e)
            }
        }
    }
}
