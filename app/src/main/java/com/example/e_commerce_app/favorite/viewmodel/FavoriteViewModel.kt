import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerce_app.model.product.Product
import com.example.e_commerce_app.model.repo.ShopifyRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val shopifyRepo: ShopifyRepo) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Product>>(emptyList())
    val favorites: StateFlow<List<Product>> = _favorites

    fun getAllFavorites() {
        viewModelScope.launch {
            try {
                val favoriteProducts = shopifyRepo.getAllFavorites()
                _favorites.value = favoriteProducts
            } catch (e: Exception) {
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
