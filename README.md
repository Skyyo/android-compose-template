# android-compose-template
Project with various common components, to reduce "project setup" operations



be very careful when choosing between ```liveData``` & ```stateFlows```. 
We still can't drop ```liveData``` not only because we need it in the ```savedStateHandle.getLiveData<Key>``` scenarios, but because ```stateFlow``` can't reproduce a certain behaviour in "search-like" scenarios:
```kotlin
class ViewModel(repository: Repository) : ViewModel() {

    private val query = MutableStateFlow("")
    
    val results: Flow<Result> = query.flatMapLatest { query ->
        repository.search(query)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = Result()
    )
    
   fun onQueryChanged(query: String) { query.value = query }
}
```
Always use ```liveData``` for cases when we are performing ```observable.switchMap/flatMapLatest``` type of operations. In code above it's the ```query```, it has to be declared as ```liveData```. You can always observe it using ```.asFlow```
Sooner or later the behaviour of ```stateFlow``` will be changed, and we'll be able to ditch the ```liveData``` once and for all.
Behaviour difference is explained [here](https://github.com/Kotlin/kotlinx.coroutines/issues/2223) 

- always attempt to use function references

Compose
- use [ViewCompositionStrategy](https://developer.android.com/jetpack/compose/interop/interop-apis#composition-strategy) when using compose with fragments as containers
- use [@Immutable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Immutable) annotations for immutable classes
- give keys to lazy columns and everywhere where use iterate over items like items.foreach, since [keys prevent recomposing unchanged items](https://developer.android.com/jetpack/compose/lifecycle#composition-anatomy) 
