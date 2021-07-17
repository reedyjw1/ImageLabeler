import com.reedy.imagelabeler.features.annotations.repository.AnnotationsRepository
import com.reedy.imagelabeler.features.annotations.repository.IAnnotationsRepository
import com.reedy.imagelabeler.utils.shared.ISharedPrefsHelper
import com.reedy.imagelabeler.utils.shared.SharedPrefsHelper
import org.koin.dsl.module

val appDependencies = module {
    // Singleton (returns always the same unique instance of the object)
    single<IAnnotationsRepository> { AnnotationsRepository(get()) }
    single<ISharedPrefsHelper> { SharedPrefsHelper(get()) }
}