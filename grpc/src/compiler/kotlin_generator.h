/*
 * Copyright 2016 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#ifndef NET_GRPC_COMPILER_KOTLIN_GENERATOR_H_
#define NET_GRPC_COMPILER_KOTLIN_GENERATOR_H_

#include <stdlib.h>  // for abort()

#include "src/compiler/schema_interface.h"

namespace grpc_kotlin_generator {
struct Parameters {
  //        Defines the custom parameter types for methods
  //        eg: flatbuffers uses flatbuffers.Builder as input for the client
  //        and output for the server grpc::string custom_method_io_type;

  // Package name for the service
  grpc::string package_name;
};

// Return the source of the generated service file.
grpc::string GenerateServiceSource(grpc_generator::File* file,
                                   const grpc_generator::Service* service,
                                   grpc_kotlin_generator::Parameters* parameters);

}  // namespace grpc_kotlin_generator

#endif  // NET_GRPC_COMPILER_KOTLIN_GENERATOR_H_
