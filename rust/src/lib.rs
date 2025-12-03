use jni::JNIEnv;
use jni::objects::{JClass, JString};
use std::fs::OpenOptions;
use std::io::Write;
use std::path::Path;
use chrono::Local;

#[no_mangle]
pub extern "system" fn Java_com_example_myspamfilterapp_SpamLogger_logNumber(
    mut env: JNIEnv,
    _class: JClass,
    entry: JString,
) {
    let entry_str: String = env.get_string(&entry).expect("Couldn't get Java string!").into();

    let file_path = "/data/data/com.example.myspamfilterapp/files/spam_calls.log";
    let path = Path::new(file_path);

    let mut file = OpenOptions::new()
        .create(true)
        .append(true)
        .open(path)
        .expect("Unable to open log file");

    let log_entry = format!("{} - {}\n", Local::now().format("%Y-%m-%d %H:%M:%S"), entry_str);
    file.write_all(log_entry.as_bytes()).expect("Unable to write log");
}
