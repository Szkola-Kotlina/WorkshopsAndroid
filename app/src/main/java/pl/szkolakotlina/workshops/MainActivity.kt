package pl.szkolakotlina.workshops

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import pl.szkolakotlina.workshops.ui.theme.AndroidWorkshopsTheme
import timber.log.Timber

class RssListViewModel : ViewModel() {

  val items: MutableStateFlow<List<Article>> =
    MutableStateFlow(emptyList())

  init {
    //    val data = fetchData()
    viewModelScope.launch {
      val fetchData = fetchData()
      items.value = fetchData
    }
  }
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      AndroidWorkshopsTheme {
        // A surface container using the 'background' color from the theme
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colors.background
        ) {
          RssFeedList()
        }
      }
    }
  }
}

data class Article(
  val title: String,
  val link: String,
  val publishDate: String,
  val image: String
)

private suspend fun fetchData(): List<Article> {
  val rssFeed = NetworkModule.createApi().getRssFeed()

  return rssFeed.items?.map {
    Article(
      title = it.title ?: "",
      image = it.image ?: "https://picsum.photos/3000/1000?grayscale",
      link = it.link ?: "",
      publishDate = it.publishDate ?: ""
    )
  } ?: emptyList()
}

@Composable
fun RssFeedList() {

  val viewModel = remember {
    RssListViewModel()
  }

  val state = viewModel.items
    .collectAsState()

  val data = state.value

  Box(contentAlignment = Alignment.TopCenter) {
    LazyColumn(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
    ) {
      items(data) {
        ArticleViewItem(it)
      }
    }
  }
}

@Composable
fun ArticleViewItem(article: Article) {

  // Obrazek - na cała szerokość
  // Tytuł: duży tekst
  // Published date - mały tekst
  // link – na niebiesko

  Timber.d("image: ${article.image}")

  Box(
    modifier = Modifier.padding(16.dp)
  ) {

    Column() {
      Row() {
        Box() {
          AsyncImage(
            model = article.image,
            contentScale = ContentScale.Crop,
            contentDescription = article.title,
            modifier = Modifier
              .height(250.dp)
              .fillMaxWidth()
          )
          Text(
            text = article.title,
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .background(Color.DarkGray)
              .padding(8.dp),
            textAlign = TextAlign.Center,
            color = Color.White,
            style = MaterialTheme.typography.h5
          )
        }
      }
      Row() {
        Text(text = article.publishDate)
      }
      Row() {
        TextButton(onClick = {
          Timber.d("Clicked")
        }) {
          Text(text = "Przeczytaj calosc")
        }
      }
    }

  }

}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
  AndroidWorkshopsTheme {
    RssFeedList()
  }
}