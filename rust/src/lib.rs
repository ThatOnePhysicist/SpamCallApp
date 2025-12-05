//! JNI library for logging spam call entries to a file.
//!
//! This library exposes a single function to Java via the JNI interface,
//! allowing the Android app to log spam call entries to a persistent log file.
//!
//! # Functions
//!
//! - `Java_com_example_myspamfilterapp_SpamLogger_logNumber`
//!   Logs a string entry with a timestamp to `spam_calls.log` in the app's
//!   private files directory.

use jni::JNIEnv;
use jni::objects::{JClass, JString};
use std::fs::OpenOptions;
use std::io::Write;
use std::path::Path;
use chrono::Local;

/// Writes a timestamped log entry from the Android app to `spam_calls.log`.
///
/// # Safety
/// This function is exposed via JNI and must be called from Java.
/// It will panic if it cannot convert the Java string or access the log file.
///
/// # Parameters
/// - `env`: JNI environment pointer.
/// - `_class`: Java class reference (unused, required by JNI signature).
/// - `entry`: A Java string containing the log entry.
#[no_mangle]
pub extern "system" fn Java_com_example_myspamfilterapp_SpamLogger_logNumber(
    mut env: JNIEnv,
    _class: JClass,
    entry: JString,
    path_str: JString,
) {
    let entry_str: String = env.get_string(&entry)
    .expect("Couldn't get Java string!")
    .into();

    let path_value: String = env.get_string(&path_str)
    .expect("Couldn't get the log path string!")
    .into();

    let path = Path::new(&path_value);

    let mut file = OpenOptions::new()
        .create(true)
        .append(true)
        .open(path)
        .expect("Unable to open log file");

    let log_entry = format!(
    "{} - {}\n",
    Local::now().format("%Y-%m-%d %H:%M:%S"),
    entry_str
    );

    file.write_all(log_entry.as_bytes()).expect("Unable to write log");
}
