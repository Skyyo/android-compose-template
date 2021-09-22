# android-compose-template
Project with various common components, to reduce "project setup" operations



Be very careful when choosing between ```liveData``` & ```stateFlows```. 
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

Be carefull how you update the ```stateFlow``` value, since using ```stateFlow.value = stateFlow.value.copy()``` can create unexpected results. If between the time copy function completes and the ```stateFlows``` new value is emitted another thread tries to update the ```stateFlow``` — by using copy and updating one of the properties that the current copy isn’t modifying — we could end up with results we were not expecting. So please use [update](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/update.html) in such cases. 



- always attempt to use function references

Compose
- use [ViewCompositionStrategy](https://developer.android.com/jetpack/compose/interop/interop-apis#composition-strategy) when using compose with fragments as containers
- use [@Immutable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Immutable) annotations for immutable classes
- give keys to lazy columns and everywhere where use iterate over items like items.foreach, since [keys prevent recomposing unchanged items](https://developer.android.com/jetpack/compose/lifecycle#composition-anatomy) 


# License
```
MIT License

Copyright (c) 2021 Denis Rudenko

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.```
