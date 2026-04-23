import { ref } from 'vue'

export const toastRef = ref<any>(null)

export function useToast() {
  function success(title: string, message?: string) {
    return toastRef.value?.addToast('success', title, message)
  }

  function error(title: string, message?: string) {
    return toastRef.value?.addToast('error', title, message)
  }

  function info(title: string, message?: string) {
    return toastRef.value?.addToast('info', title, message)
  }

  function warning(title: string, message?: string) {
    return toastRef.value?.addToast('warning', title, message)
  }

  function remove(id: string) {
    toastRef.value?.removeToast(id)
  }

  function clear() {
    toastRef.value?.clearAll()
  }

  return {
    success,
    error,
    info,
    warning,
    remove,
    clear,
  }
}
