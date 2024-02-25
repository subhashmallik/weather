# Install Android SDK
RUN apt-get update && apt-get install -y openjdk-8-jdk-headless && apt-get install -y wget && apt-get install -y unzip
RUN wget https://dl.google.com/android/repository/sdk-tools-linux-3859397.zip && unzip sdk-tools-linux-3859397.zip && mv tools /opt/android-sdk
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$ANDROID_HOME/tools:$ANDROID_HOME/tools/bin:$ANDROID_HOME/platform-tools:$PATH
RUN echo y | sdkmanager "platforms;android-33" && sdkmanager "build-tools;33.0.3"

# Build the app
COPY . /app
RUN cd /app && ./gradlew assembleRelease

# Run the app
CMD ["/app/app/build/outputs/apk/release/app-release.apk"]